package edu.upb.barber.service.integracion;

import edu.upb.barber.repository.dto.request.ClienteRequestDto;
import edu.upb.barber.repository.dto.request.EmpresaRequestDto;
import edu.upb.barber.repository.dto.request.UsuarioRequestDto;
import edu.upb.barber.repository.dto.response.ClienteResponseDto;
import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
import edu.upb.barber.repository.dto.response.UsuarioResponseDto;
import edu.upb.barber.service.exception.NotDataFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;


@Slf4j
@Service
@ConditionalOnProperty(name = "sistema1.enabled", havingValue = "true", matchIfMissing = false)
public class SistemaA {

    @Value("${sistema1.url-base}")
    private String urlBase;
    @Value("${sistema1.connect-timeout}")
    private int connectTimeout = 10000;
    @Value("${sistema1.read-timeout}")
    private int readTimeout = 40000;


    public Sistema1AuthResponse auth(Sistema1AuthRequest request) throws Exception {
        RestClient restClient = create();

        ResponseEntity<Sistema1AuthResponse> response;
        try {
            response = restClient.post()
                    .uri(urlBase + "/api/v1/auth/login")
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .toEntity(Sistema1AuthResponse.class);
        } catch (NotDataFoundException e) {
            log.error("NotDataFoundException. {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Se genero error: {}", response.getStatusCode().value());
            throw new Exception("Se genero error");
        }

        return response.getBody();
    }

    public List<EmpresaResponseDto> listarEmpresa(String token) throws Exception {

        RestClient restClient = create();

        ResponseEntity<List<EmpresaResponseDto>> response;

        try {

            response = restClient.get()
                    .uri(urlBase + "/api/v1/empresas")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<EmpresaResponseDto>>() {});

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }

    public EmpresaResponseDto crearEmpresa(String token, EmpresaRequestDto request) throws Exception {

        RestClient restClient = create();

        ResponseEntity<EmpresaResponseDto> response;

        try {

            response = restClient.post()
                    .uri(urlBase + "/api/v1/empresas")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .toEntity(EmpresaResponseDto.class);

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }


    public EmpresaResponseDto actualizarEmpresa(String token, Long id, EmpresaRequestDto request) throws Exception {

        RestClient restClient = create();

        ResponseEntity<EmpresaResponseDto> response;

        try {

            response = restClient.put()
                    .uri(urlBase + "/api/v1/empresas/" + id)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .toEntity(EmpresaResponseDto.class);

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }

    public List<UsuarioResponseDto> listarUsuarios(String token) throws Exception {

        RestClient restClient = create();

        ResponseEntity<List<UsuarioResponseDto>> response;

        try {

            response = restClient.get()
                    .uri(urlBase + "/api/v1/usuarios")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<UsuarioResponseDto>>() {});

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }
    public UsuarioResponseDto crearUsuario(String token, UsuarioRequestDto request) throws Exception {

        RestClient restClient = create();

        ResponseEntity<UsuarioResponseDto> response;

        try {

            response = restClient.post()
                    .uri(urlBase + "/api/v1/usuarios")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .toEntity(UsuarioResponseDto.class);

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }

    public UsuarioResponseDto actualizarUsuario(String token, Long id, UsuarioRequestDto request) throws Exception {

        RestClient restClient = create();

        ResponseEntity<UsuarioResponseDto> response;

        try {

            response = restClient.put()
                    .uri(urlBase + "/api/v1/usuarios/" + id)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .toEntity(UsuarioResponseDto.class);

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }

    public List<ClienteResponseDto> listarClientes(String token) throws Exception {

        RestClient restClient = create();

        ResponseEntity<List<ClienteResponseDto>> response;

        try {

            response = restClient.get()
                    .uri(urlBase + "/api/v1/clientes")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<ClienteResponseDto>>() {});

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }

    public ClienteResponseDto crearCliente(String token, ClienteRequestDto request) throws Exception {

        RestClient restClient = create();

        ResponseEntity<ClienteResponseDto> response;

        try {

            response = restClient.post()
                    .uri(urlBase + "/api/v1/clientes")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .toEntity(ClienteResponseDto.class);

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }

    public ClienteResponseDto actualizarCliente(String token, Long id, ClienteRequestDto request) throws Exception {

        RestClient restClient = create();

        ResponseEntity<ClienteResponseDto> response;

        try {

            response = restClient.put()
                    .uri(urlBase + "/api/v1/clientes/" + id)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .retrieve()
                    .toEntity(ClienteResponseDto.class);

        } catch (Exception e) {
            log.error("Exception. ", e);
            throw e;
        }

        return response.getBody();
    }


    private RestClient create() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(Duration.ofMillis(connectTimeout));
        clientHttpRequestFactory.setReadTimeout(Duration.ofMillis(readTimeout));

        return RestClient.builder().requestFactory(clientHttpRequestFactory).build();
    }

}
