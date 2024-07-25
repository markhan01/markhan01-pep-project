package Service;

import java.util.List;
import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService() {
        this.messageDAO = new MessageDAO();
    }

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public Message addMessage(Message message) {
        return this.messageDAO.insertMessage(message);
    }

    public List<Message> getAllMessages() {
        return this.messageDAO.getAllMessages();
    }

    public Message getMessageById(int id) {
        return this.messageDAO.getMessageById(id);
    }

    public Message updateMessage(String text, int id) {
        return this.messageDAO.updateMessage(text, id);
    }

    public Message deleteMessage(int id) {
        return this.messageDAO.deleteMessage(id);
    }

    public List<Message> getAllMessagesById(int id) {
        return this.messageDAO.getAllMessagesById(id);
    }
}
