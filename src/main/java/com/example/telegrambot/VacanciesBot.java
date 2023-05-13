package com.example.telegrambot;

import com.example.telegrambot.dto.VacancyDto;
import com.example.telegrambot.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private VacancyService vacancyService;

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
                } else if ("showMiddleVacancies".equals(callbackData)) {
                    showMiddleVacancies(update);
                } else if ("showSeniorVacancies".equals(callbackData)) {
                    showSeniorVacancies(update);
                } else if (callbackData.startsWith("vacancyId=")) {
                    // vacancyId=1 => (vacancyId) + (1) => (1)
                    String id = callbackData.split("=")[1];
                    showVacancyDescription(id, update);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't send message to user!", e);
        }
    }

    private void showVacancyDescription(String id, Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        VacancyDto vacancy = vacancyService.get(id);
        String description = vacancy.getShortDescription();
        sendMessage.setText(description);
        execute(sendMessage);
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
        // receive list of only junior vacancies
        List<VacancyDto> vacancies = vacancyService.getJuniorVacancies();
        // for every vacancy generate buttons
        for (VacancyDto vacancy: vacancies) {
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(vacancy.getTitle());
            vacancyButton.setCallbackData("vacancyId=" + vacancy.getId());
            row.add(vacancyButton);
        }

        // returning actual keyboard
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }

    private void showMiddleVacancies(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose vacancy:");
        // get chatId of the user who clicked the button
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        // add new menu for junior vacancies
        sendMessage.setReplyMarkup(getMiddleVacanciesMenu());
        // method call
        execute(sendMessage);
    }

    private ReplyKeyboard getMiddleVacanciesMenu() {
        // buttons
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton maVacancy = new InlineKeyboardButton();
        maVacancy.setText("Middle Java developer at MA");
        maVacancy.setCallbackData("vacancyId=3");
        row.add(maVacancy);

        InlineKeyboardButton googleVacancy = new InlineKeyboardButton();
        googleVacancy.setText("Middle Java developer at Google");
        googleVacancy.setCallbackData("vacancyId=4");
        row.add(googleVacancy);

        // returning actual keyboard
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }

    private void showSeniorVacancies(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose vacancy:");
        // get chatId of the user who clicked the button
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        // add new menu for junior vacancies
        sendMessage.setReplyMarkup(getSeniorVacanciesMenu());
        // method call
        execute(sendMessage);
    }

    private ReplyKeyboard getSeniorVacanciesMenu() {
        // buttons
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton maVacancy = new InlineKeyboardButton();
        maVacancy.setText("Senior Java developer at MA");
        maVacancy.setCallbackData("vacancyId=5");
        row.add(maVacancy);

        InlineKeyboardButton googleVacancy = new InlineKeyboardButton();
        googleVacancy.setText("Senior Java developer at Google");
        googleVacancy.setCallbackData("vacancyId=6");
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
