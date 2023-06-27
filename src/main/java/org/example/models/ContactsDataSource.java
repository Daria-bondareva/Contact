package org.example.models;

import org.example.converters.JsonConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ContactsDataSource {

    private final JsonConverter jsonConverter;
    private final Path path_file;

    public ContactsDataSource(JsonConverter gsonConverter, Path path_file) {
        this.jsonConverter = gsonConverter;
        this.path_file = path_file;
    }

    public List<Contact> readContacts() {
        try{
            String jsonContacts = Files.readString(path_file);
            return jsonConverter.fromJson(jsonContacts);
        }catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void writeContacts(List<Contact> contacts) {
        String jsonContacts = jsonConverter.toJson(contacts);
        try {
            Files.writeString(path_file, jsonContacts);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
