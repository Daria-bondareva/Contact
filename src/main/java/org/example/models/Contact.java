package org.example.models;

import java.time.LocalDate;

public record Contact(FullName fullName, String phoneNumber, String email, LocalDate birthday, String address) {
}
