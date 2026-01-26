package edu.comillas.icai.gitt.pat.spring.mvc;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
public class TareasProgramadas {
    private Logger logger = LoggerFactory.getLogger(getClass());

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

    }
}