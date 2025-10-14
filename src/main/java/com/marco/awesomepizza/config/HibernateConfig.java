package com.marco.awesomepizza.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(basePackages = {"com.marco.awesomepizza.menu.repository"})
public class HibernateConfig {
}