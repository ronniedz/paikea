package cab.bean.srvcs.tube4kids.health;

import com.codahale.metrics.health.HealthCheck;
import cab.bean.srvcs.tube4kids.core.Template;

import java.util.Optional;

public class TemplateHealthCheck extends HealthCheck {
    private final Template template;

    public TemplateHealthCheck(Template template) {
        this.template = template;
    }

    @Override
    protected Result check() throws Exception {
        template.render(Optional.of("woo"));
        template.render(Optional.empty());
        return Result.healthy();
    }
}
