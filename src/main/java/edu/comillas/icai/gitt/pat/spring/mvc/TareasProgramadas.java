package edu.comillas.icai.gitt.pat.spring.mvc;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class TareasProgramadas {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String respuesta_ant;
    @Scheduled(fixedRate = 300000)
    public void ritmoFijo() {
        logger.info("Me ejecuto cada 5 minutos");
    }

    @Scheduled(cron = "0 * * * * *")
    public void expresionCron() {
        logger.info("Me ejecuto cuando empieza un nuevo minuto");
    }

    @Scheduled(fixedRate = 60000)
    public void consultarAPI() {
        //hola
        logger.info("La url es: https://xkcd.com/info.0.json");
        try {
            HttpHeaders headers = new HttpHeaders();
            //headers.set("Cabecera", "Valor");
            //RestTemplate restTemplate=new RestTemplate();

            ResponseEntity<String> response = new RestTemplate().exchange(
                    "https://xkcd.com/info.0.json", HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
            //=  restTemplate.getForObject("https://xkcd.com/info.0.json", String.class);


            if (response.getStatusCode()== HttpStatus.OK) {
                String respuesta = response.getBody();
                logger.info("He recibido respuesta");
                assert respuesta != null;
                if (!respuesta.equals(respuesta_ant)) {
                    logger.info(respuesta);
                }
                this.respuesta_ant=respuesta;
            }




        } catch (HttpStatusCodeException e) {
            logger.error("Error {} en la respuesta", e.getStatusCode());
        } catch (Exception e) {
            logger.error("Error inesperado en la llamada del API", e);
        }
    }
}