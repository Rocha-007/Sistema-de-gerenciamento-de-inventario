package com.projeto.inventario;

import com.projeto.inventario.ui.LoginScreen;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

@SpringBootApplication
@EnableMethodSecurity
public class InventarioApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(InventarioApplication.class);
		application.setHeadless(false);
		application.run(args);

		if (!GraphicsEnvironment.isHeadless()) {
			SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
		}
	}

}
