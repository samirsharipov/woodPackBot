package uz.ermatov.woodpack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.ermatov.woodpack.telegram.WoodPackTelegramBot;

@SpringBootApplication
public class WoodPackApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(WoodPackApplication.class, args);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            WoodPackTelegramBot bot = context.getBean(WoodPackTelegramBot.class); // ✅ Spring kontekstidan olish
            botsApi.registerBot(bot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

