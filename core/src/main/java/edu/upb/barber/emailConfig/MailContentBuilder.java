package edu.upb.barber.emailConfig;

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


    public String sendPassword(String password) {
        final Context ctx = new Context();
        ctx.setVariable("password", password);
        ctx.setVariable("imageResourceName", "banner");
        ctx.setVariable("imageX", "imageX");
        ctx.setVariable("imageLinkedin", "imageLinkedin");

        ctx.setVariable("clienteNombre", "Ricardo Laredo");
        ctx.setVariable("servicioNombre", "Corte Premium");
        ctx.setVariable("empleadoNombre", "Jose Antonio Luque");
        ctx.setVariable("citaFecha", "Miercoles 17 de Junio de 2026");
        ctx.setVariable("citaHora", "19:00");
        ctx.setVariable("sucursalNombre", "Klipp Pirai");
        ctx.setVariable("precioTotal", "80 Bs.");
        ctx.setVariable("urlCitas", "https://www.upbvirtual.net/upbvirtual/login/index.php");
        //Lo de urlCitas hay que pensarlo porque lo mas probable es que no se pueda usar cuando este este desplegado
        //pensar en una opcion de que se pueda agregar a google calendar


        return this.templateEngine.process("citaConfirmada", ctx);
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
