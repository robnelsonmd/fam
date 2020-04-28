package fam.puzzle;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

@TestConfiguration
public class TestContext {
    @Bean
    public String applicationVersion() {
        return "test-app-version";
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(Arrays.asList(TestUtil.JOE, TestUtil.ADMIN));
    }
}
