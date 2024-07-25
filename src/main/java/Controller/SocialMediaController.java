package Controller;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postRegisterHandler);
        app.post("/login", this::postLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::patchMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessageByAccountIdHandler);
        
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void postRegisterHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
            ctx.status(400);
            return;
        }

        Account addedAccount = accountService.addAccount(account);
        if (addedAccount != null) {
            ctx.json(mapper.writeValueAsString(addedAccount));
        } else {
            ctx.status(400);
        }
    }

    private void postLoginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account loginAccount = accountService.getAccount(account);
        if (loginAccount != null) {
            ctx.json(mapper.writeValueAsString(loginAccount));
        } else {
            ctx.status(401);
        }
    }

    private void postMessageHandler (Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        
        if (validateMessage(message) == false) {
            ctx.status(400);
            return;
        }

        if (accountService.getAccountById(message.getPosted_by()) == null) {
            System.out.println("Cannot find account of id: " + message.getPosted_by());
            ctx.status(400);
            return;
        }

        Message addedMessage = messageService.addMessage(message);
        if (addedMessage != null) {
            ctx.json(mapper.writeValueAsString(addedMessage));
        } else {
            ctx.status(400);
        }
    }

    private void getAllMessagesHandler (Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    private void getMessageByIdHandler (Context ctx) {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));
        Message message = messageService.getMessageById(id);
        if (message != null) ctx.json(message);
    }

    private void deleteMessageHandler (Context ctx) {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));
        if (findMessage(id)) ctx.json(messageService.deleteMessage(id));
    }

    private void patchMessageHandler (Context ctx) throws JsonProcessingException {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("message_id")));
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        
        if (validateMessage(message) == false || findMessage(id) == false) {
            ctx.status(400);
            return;
        }

        Message updatedMessage = messageService.updateMessage(message.getMessage_text(), id);
        if (updatedMessage != null) {
            ctx.json(mapper.writeValueAsString(updatedMessage));
        } else {
            ctx.status(400);
        }
    }

    private void getMessageByAccountIdHandler (Context ctx) {
        int id = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("account_id")));
        List<Message> messages = messageService.getAllMessagesById(id);
        ctx.json(messages);

    }

    private boolean validateMessage(Message message) {
        if (message.getMessage_text().isBlank()) {
            System.out.println("Blank Message");
            return false;
        }

        if (message.getMessage_text().length() > 254) {
            System.out.println("Message not under 255 characters");
            return false;
        }

        return true;
    }

    private boolean findMessage(int id) {
        if (messageService.getMessageById(id) == null) {
            System.out.println("Message ID not found");
            return false;
        }
        return true;
    }
}