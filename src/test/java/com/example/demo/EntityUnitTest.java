package com.example.demo;

import com.example.demo.entities.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class EntityUnitTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testDoctorEntity() {
        Doctor doctor = new Doctor("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        entityManager.persist(doctor);
        entityManager.flush();
        // Recupera el doctor de la base de datos
        Doctor foundDoctor = entityManager.find(Doctor.class, doctor.getId());
        // Verifica que el doctor recuperado sea igual al doctor persistido
        assertThat(foundDoctor).isEqualTo(doctor);
    }

    @Test
    void testDoctorSetId() {
        // Crear una instancia de Doctor
        Doctor doctor = new Doctor("John", "Doe", 35, "john.doe@example.com");

        // Establecer un ID inicial
        long initialId = 1L;
        doctor.setId(initialId);

        // Verificar que el ID inicial se estableció correctamente
        assertThat(doctor.getId()).isEqualTo(initialId);
    }

    @Test
    void testPatientEntity() {
        Patient patient = new Patient("Perla", "Amalia", 24, "p.amalia@hospital.accwe");
        entityManager.persist(patient);
        entityManager.flush();

        Patient foundPatient = entityManager.find(Patient.class, patient.getId());

        assertThat(foundPatient).isEqualTo(patient);
    }

    @Test
    void testPatientSetId() {
        Patient patient = new Patient("Perla", "Amalia", 24, "p.amalia@hospital.accwe");

        long initialId = 1L;
        patient.setId(initialId);

        assertThat(patient.getId()).isEqualTo(initialId);
    }

    @Test
    void testRoomEntity() {
        Room room = new Room("Dermatology");
        entityManager.persist(room);
        entityManager.flush();

        Room foundRoom = entityManager.find(Room.class, room.getRoomName());

        assertThat(foundRoom).isEqualTo(room);
    }

    @Test
    void testAppointmentSetId() {
        Appointment appointment = new Appointment();

        long initialId = 1L;
        appointment.setId(initialId);

        // Verificar que el ID inicial se estableció correctamente
        assertThat(appointment.getId()).isEqualTo(initialId);
    }

    @Test
    public void testAppointmentConstructorAndGetters() {
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        Room room = new Room();
        LocalDateTime startsAt = LocalDateTime.of(2024, 2, 21, 9, 0);
        LocalDateTime finishesAt = LocalDateTime.of(2024, 2, 21, 10, 0);

        Appointment appointment = new Appointment(patient, doctor, room, startsAt, finishesAt);

        assertThat(appointment.getPatient()).isEqualTo(patient);
        assertThat(appointment.getDoctor()).isEqualTo(doctor);
        assertThat(appointment.getRoom()).isEqualTo(room);
        assertThat(appointment.getStartsAt()).isEqualTo(startsAt);
        assertThat(appointment.getFinishesAt()).isEqualTo(finishesAt);
    }

    @Test
    public void testAppointmentSetterAndGetters() {
        Appointment appointment = new Appointment();
        Patient patient = new Patient();
        Doctor doctor = new Doctor();
        Room room = new Room();
        LocalDateTime startsAt = LocalDateTime.of(2024, 2, 21, 9, 0);
        LocalDateTime finishesAt = LocalDateTime.of(2024, 2, 21, 10, 0);

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setRoom(room);
        appointment.setStartsAt(startsAt);
        appointment.setFinishesAt(finishesAt);

        assertThat(appointment.getPatient()).isEqualTo(patient);
        assertThat(appointment.getDoctor()).isEqualTo(doctor);
        assertThat(appointment.getRoom()).isEqualTo(room);
        assertThat(appointment.getStartsAt()).isEqualTo(startsAt);
        assertThat(appointment.getFinishesAt()).isEqualTo(finishesAt);
    }

    @Test
    public void testCitasNoSeSuperponen() {
        // Arrange
        Room room = new Room("Dermatología");
        Appointment citaA = new Appointment(null, null, room, LocalDateTime.of(2024, 2, 21, 9, 0), LocalDateTime.of(2024, 2, 21, 12, 0));
        Appointment citaB = new Appointment(null, null, room, LocalDateTime.of(2024, 2, 21, 13, 0), LocalDateTime.of(2024, 2, 21, 15, 0));

        assertThat(citaA.overlaps(citaB)).isFalse();
    }

    @Test
    void testCitasSuperposicionFinal() {
        Room room = new Room("Dermatología");
        Appointment citaA = new Appointment(null, null, room, LocalDateTime.of(2024, 2, 21, 16, 0), LocalDateTime.of(2024, 2, 21, 15, 0));
        Appointment citaB = new Appointment(null, null, room, LocalDateTime.of(2024, 2, 21, 13, 0), LocalDateTime.of(2024, 2, 21, 15, 0));

        assertThat(citaA.overlaps(citaB)).isTrue();
    }

    @Test
    void testCitasSuperposicionDuranteFin() {
        Room room = new Room("Dermatología");
        Appointment citaA = new Appointment(null, null, room, LocalDateTime.of(2024, 2, 20, 05, 0), LocalDateTime.of(2024, 2, 21, 15, 0));
        Appointment citaB = new Appointment(null, null, room, LocalDateTime.of(2024, 2, 19, 13, 0), LocalDateTime.of(2024, 2, 20, 15, 0));

        assertThat(citaA.overlaps(citaB)).isTrue();
    }

    @Test
    void testCitasSuperposicionDuranteInicio() {
        Room room = new Room("Dermatología");
        Appointment citaA = new Appointment(null, null, room, LocalDateTime.of(2024, 2, 21, 05, 0), LocalDateTime.of(2024, 2, 21, 15, 0));
        Appointment citaB = new Appointment(null, null, room, LocalDateTime.of(2024, 2, 21, 13, 0), LocalDateTime.of(2024, 2, 21, 16, 0));

        assertThat(citaA.overlaps(citaB)).isTrue();
    }

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        int age = 30;
        String email = "john.doe@example.com";

        // Act
        Person person = new Person(firstName, lastName, age, email);

        // Assert
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
        assertEquals(age, person.getAge());
        assertEquals(email, person.getEmail());
    }

    @Test
    public void testSetters() {
        // Arrange
        Person person = new Person();

        // Act
        String firstName = "Jane";
        String lastName = "Smith";
        int age = 25;
        String email = "jane.smith@example.com";

        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAge(age);
        person.setEmail(email);

        // Assert
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
        assertEquals(age, person.getAge());
        assertEquals(email, person.getEmail());
    }
}
