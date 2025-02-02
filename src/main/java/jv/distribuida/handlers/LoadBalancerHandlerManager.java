package jv.distribuida.handlers;

import com.google.gson.*;
import jv.distribuida.loadbalancer.ServiceInfo;
import jv.distribuida.loadbalancer.ServiceInstance;
import jv.distribuida.network.ConnectionType;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoadBalancerHandlerManager implements RequestHandler {

    private final static String missingTarget = "{\"status\":\"Failure\",\"message\":\"The 'target' attribute was not found\"}";
    private final static String targetNotFound = "{\"status\":\"Failure\",\"message\":\"Target: $$$ was not found\"}";
    protected final HashMap<String, ServiceInfo> services;
    protected final ConnectionType type;
    private final ScheduledThreadPoolExecutor heartBeatExecutor = new ScheduledThreadPoolExecutor(1);

    public LoadBalancerHandlerManager(ConnectionType type) {
        this.services = new HashMap<>();
        this.type = type;
        heartBeatExecutor.scheduleWithFixedDelay(this::heartbeat, 2, 2, TimeUnit.SECONDS);
    }

    public void disableHeartbeat() {
        heartBeatExecutor.shutdown();
        heartBeatExecutor.close();
    }

    @Override
    public Message handle(Message message) {
        try {
            JsonObject json = JsonParser.parseString(message.getText()).getAsJsonObject();
            JsonElement targetElem = json.get("target");
            if (targetElem == null) {
                message.setText(missingTarget);
                return message;
            }
            String target = targetElem.getAsString();
            if (target.equals("LoadBalancer")) { // load balancer service
                json.addProperty("address", message.getAddress().getHostAddress());
                message.setText(handleLoadBalancer(json));
            } else { // Other service
                message.setText(handleService(target, json));
            }
            return message;
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            message.setText(exceptionHandler(e.getMessage()));
            return message;
        }
    }

    void heartbeat() {
        for (String key : services.keySet().stream().toList()) {
            if (!services.get(key).heartbeat()) {
                System.out.println(key + " removed because none of its services is alive");
                services.remove(key);
            } else {
                System.out.println(key + " alive with " + services.get(key).size() + " running services");
            }
        }
    }

    void authorize(JsonObject json) throws IOException {
        if (!services.containsKey("Auth")) {
            throw new IOException("Auth service not found");
        }
        ServiceInfo info = services.get("Auth");
        JsonObject request = new JsonObject();
        request.add("token", json.get("token"));
        request.addProperty("action", "AUTHORIZE");

        JsonObject response = JsonParser.parseString(info.redirect(request.toString()).getText()).getAsJsonObject();
        if (response.get("status").getAsString().equals("Failure")) {
            throw new IOException(response.get("message").getAsString());
        }
    }

    String exceptionHandler(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Failure");
        response.addProperty("message", message);
        return response.toString();
    }

    protected String handleLoadBalancer(JsonObject json) throws IOException {
        JsonElement serviceElem = json.get("service");
        JsonElement addressElem = json.get("address");
        JsonElement portElem = json.get("port");
        JsonElement hbElem = json.get("heartbeat");
        JsonElement authElem = json.get("auth");
        JsonObject response = new JsonObject();
        if (serviceElem != null && addressElem != null &&
                portElem != null && authElem != null && hbElem != null) {
            String service = serviceElem.getAsString();
            String address = addressElem.getAsString();
            int port = portElem.getAsInt();
            int heartbeat = hbElem.getAsInt();
            boolean auth = authElem.getAsBoolean();
            if (!services.containsKey(service)) {
                services.put(service, new ServiceInfo(auth, new ArrayList<>()));
            }
            ServiceInfo serviceInfo = services.get(service);
            if (serviceInfo.isRequiresAuth() != auth) {
                throw new IOException("Service auth requirement not compatible");
            }
            ServiceInstance instance = new ServiceInstance(InetAddress.getByName(address),
                    port, heartbeat, type);
            if (!serviceInfo.contains(instance)) {
                serviceInfo.add(instance);
                System.out.println("Nova instância se registrou no service " + service +
                        " com endereço " + address + " e porta " + port);
                response.addProperty("status", "Success");
                response.addProperty("message", "Service added to known services");
            } else {
                response.addProperty("status", "Failure");
                response.addProperty("message", "Service already is a known service");
            }
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("service");
            fields.add("heartbeat");
            fields.add("auth");
            response.add("fields", fields);
        }
        return response.toString();
    }

    protected String handleService(String target, JsonObject json) throws IOException {
        if (services.containsKey(target)) {
            ServiceInfo info = services.get(target);
            if (info.isRequiresAuth()) {
                authorize(json);
            }
            return info.redirect(json.toString()).getText();
        } else {
            return targetNotFound.replace("$$$", target);
        }
    }
}