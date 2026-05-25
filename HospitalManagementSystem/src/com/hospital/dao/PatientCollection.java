package com.hospital.dao;

import com.hospital.model.Patient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * In-memory collection-based patient management (US001-Collection).
 *
 * <p>Demonstrates the OOP/Collection user story: an admin can add new patients,
 * remove by registration id, retrieve by registration id, get total count, and
 * view all. Backed by an {@link ArrayList} guarded by intrinsic locks for
 * basic thread safety in a servlet container.
 *
 * <p>This is intentionally separate from {@link PatientDAO} (DB-backed). Both
 * coexist; the application primarily uses {@link PatientDAO} for persistence,
 * but exposes this collection through a dedicated screen for the requirement.
 */
public class PatientCollection {

    private static final PatientCollection INSTANCE = new PatientCollection();
    private final List<Patient> patients = new ArrayList<>();

    private PatientCollection() { }

    public static PatientCollection getInstance() { return INSTANCE; }

    public synchronized boolean add(Patient p) {
        if (p == null || p.getPatientId() == null) return false;
        if (findById(p.getPatientId()).isPresent()) return false;
        patients.add(p);
        return true;
    }

    public synchronized boolean removeById(String patientId) {
        return patients.removeIf(p -> p.getPatientId().equals(patientId));
    }

    public synchronized Optional<Patient> findById(String patientId) {
        for (Patient p : patients) {
            if (p.getPatientId().equals(patientId)) return Optional.of(p);
        }
        return Optional.empty();
    }

    public synchronized int totalCount() {
        return patients.size();
    }

    /** Returns an unmodifiable snapshot of the current patient list. */
    public synchronized List<Patient> viewAll() {
        return Collections.unmodifiableList(new ArrayList<>(patients));
    }
}
