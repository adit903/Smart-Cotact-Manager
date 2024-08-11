package com.smartcontact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class MyConfig {
	
	
	@Bean
	public UserDetailsService userDetailsService()
	{
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
//	@Override
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//	}
	
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authenticationProvider());
//        
//    }
////	
//	 protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//	        // Configure user details service and password encoder
//	        auth.userDetailsService(getUserDetailsService()).passwordEncoder(passwordEncoder());
//	    }

	
//     
//	 protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//	    auth.authenticationProvider(authenticationProvider());
//	  }

	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		
		
		httpSecurity.csrf().disable()
		.authorizeHttpRequests()
		.requestMatchers("/admin/**").hasRole("ADMIN")
		.requestMatchers("/user/**").hasRole("USER")
		.requestMatchers("/**")
		.permitAll().anyRequest().authenticated()
		.and().formLogin().loginPage("/signin").
		loginProcessingUrl("/dologin").
		defaultSuccessUrl("/user/index")
		.failureUrl("/fail-chance");
		
		return httpSecurity.build();
		

		//requestMatchers("/user/**").hasRole("USER")
		//authorizeHttpRequests().requestMatchers("/admin/**")
		
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

}
