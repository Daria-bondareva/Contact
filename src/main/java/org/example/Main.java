package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.converters.GsonConverter;
import org.example.converters.JsonConverter;
import org.example.models.Contact;
import org.example.models.ContactsDataSource;
import org.example.repositories.AppContactRepository;
import org.example.repositories.ContactRepository;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().create();
        JsonConverter gsonConverter = new GsonConverter(gson);
        ContactsDataSource contactsDataSource = new ContactsDataSource(gsonConverter, path);
        List<Contact> contacts = contactsDataSource.readContacts();
        ContactRepository contactRepository = new AppContactRepository(contactsDataSource, contacts);

    }
}