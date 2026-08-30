package tn.knowflowai.backend.Security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class InternalApiKeyFilter
        extends OncePerRequestFilter {


    @Value("${app.internal-api-key:KNOWFLOW_SECRET_KEY}")
   
    private String internalApiKey;


    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path =
                request.getServletPath();


        // This filter only protects internal endpoints

        return !path.startsWith(
                "/api/internal/"
        );
    }


    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain

    ) throws ServletException, IOException {


        String apiKey =
                request.getHeader(
                        "X-Internal-API-Key"
                );


        if (

                apiKey == null ||

                !apiKey.equals(
                        internalApiKey
                )

        ) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(

                    """
                    {
                        "error": "Invalid internal API key"
                    }
                    """
            );

            return;
        }


        filterChain.doFilter(

                request,

                response
        );
    }
}