package com.example.taskmanager.security;

import com.example.taskmanager.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    @Mock
    JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test

    void shouldPassRequestDownTheChain_whenAuthorizationHeaderIsMissing() throws Exception {

        // Arrange


        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);


        SecurityContextHolder.clearContext();

        //Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);


    }
    @Test
    void shouldPassRequestDownTheChain_whenAuthorizationHeaderDoesNotStartWithBearer() throws  Exception{
        // Arrange


        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Basic abc123");
        FilterChain filterChain = mock(FilterChain.class);


        SecurityContextHolder.clearContext();

        //Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);
    }
    @Test
    void doFilter_shouldNotAuthenticate_whenBearerTokenIsInvalid() throws Exception {
        //Act
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.validateToken(token)).thenReturn(false);

        //Assert
        SecurityContextHolder.clearContext();
        jwtAuthenticationFilter.doFilter(request, response, filterChain);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService).validateToken(token);
        verify(filterChain).doFilter(request, response);

    }

}
