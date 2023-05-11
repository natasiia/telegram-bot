package com.example.telegrambot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class VacanciesBot extends TelegramLongPollingBot {
    public VacanciesBot() {
        super("botToken here");
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.getMessage() != null) {
                handleStartCommand(update);
            }
            if (update.getCallbackQuery() != null) {
                String callbackData = update.getCallbackQuery().getData();
                // compare received callback data
                if ("showJuniorVacancies".equals(callbackData)) {
                    showJuniorVacancies(update);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't send message to user!", e);
        }
    }

    private void showJuniorVacancies(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose vacancy:");
        // get chatId of the user who clicked the button
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        // add new menu for junior vacancies
        sendMessage.setReplyMarkup(getJuniorVacanciesMenu());
        // method call
        execute(sendMessage);
    }

    private ReplyKeyboard getJuniorVacanciesMenu() {
        // buttons
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton maVacancy = new InlineKeyboardButton();
        maVacancy.setText("Junior Java developer at MA");
        maVacancy.setCallbackData("vacancyId=1");
        row.add(maVacancy);

        InlineKeyboardButton googleVacancy = new InlineKeyboardButton();
        googleVacancy.setText("Junior Java developer at Google");
        googleVacancy.setCallbackData("vacancyId=2");
        row.add(googleVacancy);

        // returning actual keyboard
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }

    private void handleStartCommand(Update update) {
        // main logic for modifying messages (events from Telegram)
        String text = update.getMessage().getText();
        System.out.println("Received text is " + text);
        SendMessage sendMessage = new SendMessage();
        // unique identifier of the user, who sends the message to bot
        sendMessage.setChatId(update.getMessage().getChatId());
        // text of response to the user
        sendMessage.setText("Welcome to vacancies bot! Please, choose your title:");
        // method getStartMenu() to return a keyboard
        sendMessage.setReplyMarkup(getStartMenu());
        // actual response with ability to catch an error
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboard getStartMenu() {
        // buttons
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton junior = new InlineKeyboardButton();
        junior.setText("Junior");
        // message in response to user's click
        junior.setCallbackData("showJuniorVacancies");
        row.add(junior);

        InlineKeyboardButton middle = new InlineKeyboardButton();
        middle.setText("Middle");
        // message in response to user's click
        middle.setCallbackData("showMiddleVacancies");
        row.add(middle);

        InlineKeyboardButton senior = new InlineKeyboardButton();
        senior.setText("Senior");
        // message in response to user's click
        senior.setCallbackData("showSeniorVacancies");
        row.add(senior);
        // returning actual keyboard
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }

    @Override
    public String getBotUsername() {
        // return name of the bot
        return "askrypnykova vacancies bot";
    }
}
