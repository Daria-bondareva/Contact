package org.example.converters;

import org.example.models.Contact;

import java.util.List;

public interface JsonConverter {

    String toJson(List<Contact> contacts);

    List<Contact> fromJson(String contacts);
}
