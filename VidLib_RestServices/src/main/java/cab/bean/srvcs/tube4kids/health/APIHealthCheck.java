package cab.bean.srvcs.tube4kids.health;

import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.health.HealthCheck;

public class APIHealthCheck extends HealthCheck {
    private final WebTarget webTarget;

    public APIHealthCheck(Client client, String targetURL) {
        super();
        this.webTarget = client.target(targetURL);
    }

    @Override
    protected Result check() throws Exception {
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
        @SuppressWarnings("rawtypes")
        ArrayList employees = response.readEntity(ArrayList.class);
        if(employees !=null && employees.size() > 0){
            return Result.healthy();
        }
        return Result.unhealthy("API Failed");
    }
}