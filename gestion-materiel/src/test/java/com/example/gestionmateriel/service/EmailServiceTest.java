package com.example.gestionmateriel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendReservationConfirmation() {
        // Arrange
        String to = "client@example.com";
        String subject = "Confirmation de réservation";
        String body = "Votre réservation est confirmée.";

        // Act
        emailService.sendReservationConfirmation(to, subject, body);

        // Assert
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assert sentMessage.getTo() != null;
        assert sentMessage.getTo().length == 1;
        assert sentMessage.getTo()[0].equals(to);
        assert sentMessage.getSubject().equals(subject);
        assert sentMessage.getText().equals(body);
        assert sentMessage.getFrom().equals("SportSync@gmail.com");
    }


}
