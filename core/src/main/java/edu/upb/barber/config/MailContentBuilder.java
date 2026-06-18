package edu.upb.barber.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class MailContentBuilder {
    private final TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("mailTemplate", context);
    }


    public String sendPassword(String nombre, String password) {
        final Context ctx = new Context();
        ctx.setVariable("nombre", nombre);
        ctx.setVariable("password", password);
        ctx.setVariable("imageResourceName", "banner");
        ctx.setVariable("imageX", "imageX");
        ctx.setVariable("imageLinkedin", "imageLinkedin");
        return this.templateEngine.process("mailPassword", ctx);
    }

    public String sendCitaConfirmacion(String clienteNombre, String fecha, String hora,
                                       java.util.List<String> servicios,
                                       String empleadoNombre, String sucursalNombre) {
        final Context ctx = new Context();
        ctx.setVariable("clienteNombre", clienteNombre);
        ctx.setVariable("fecha", fecha);
        ctx.setVariable("hora", hora);
        ctx.setVariable("servicios", servicios);
        ctx.setVariable("empleadoNombre", empleadoNombre);
        ctx.setVariable("sucursalNombre", sucursalNombre);
        ctx.setVariable("imageX", "imageX");
        ctx.setVariable("imageLinkedin", "imageLinkedin");
        return this.templateEngine.process("mailCitaConfirmacion", ctx);
    }

    public String sendResetPassword(String nombre, String resetLink) {
        final Context ctx = new Context();
        ctx.setVariable("nombre", nombre);
        ctx.setVariable("resetLink", resetLink);
        ctx.setVariable("imageX", "imageX");
        ctx.setVariable("imageLinkedin", "imageLinkedin");
        return this.templateEngine.process("mailResetPassword", ctx);
    }

}
