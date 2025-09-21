import os
import requests

BASE = "http://localhost:8080"


def register(email, password, name, role, specialization=None, age=None):
    payload = {
        "email": email,
        "password": password,
        "name": name,
        "role": role,
        "specialization": specialization,
        "age": age
    }
    r = requests.post(f"{BASE}/api/auth/register", json=payload)
    r.raise_for_status()
    return r.json()["data"]


def login(email, password):
    payload = {"email": email, "password": password}
    r = requests.post(f"{BASE}/api/auth/login", json=payload)
    r.raise_for_status()
    data = r.json()["data"]
    return data["token"], data


def upload(token, path, patient_id=None, doctor_id=None, file_type="LAB_REPORT", description="Test Upload"):
    headers = {"Authorization": f"Bearer {token}"}
    files = {"file": open(path, "rb")}
    data = {}
    if patient_id:
        data["patientId"] = str(patient_id)
    if doctor_id:
        data["doctorId"] = str(doctor_id)
    data["fileType"] = file_type
    data["description"] = description
    r = requests.post(f"{BASE}/api/files/upload", headers=headers, files=files, data=data)
    r.raise_for_status()
    return r.json()["data"]


if __name__ == "__main__":
    # Create temp file
    test_path = "./scripts/sample.txt"
    os.makedirs("./scripts", exist_ok=True)
    with open(test_path, "w") as f:
        f.write("sample content")

    # Register doctor and patient
    try:
        register("doc@example.com", "Passw0rd!", "Dr. Who", "DOCTOR", specialization="General")
    except requests.HTTPError:
        pass
    try:
        register("pat@example.com", "Passw0rd!", "John Doe", "PATIENT", age=30)
    except requests.HTTPError:
        pass

    # Login
    token_doctor, doc_info = login("doc@example.com", "Passw0rd!")
    token_patient, pat_info = login("pat@example.com", "Passw0rd!")

    # We don't have APIs to fetch doctor/patient IDs; we will try a simple upload without IDs
    # to ensure base flow works.
    resp = upload(token_doctor, test_path, file_type="LAB_REPORT", description="Doctor upload no linkage")
    print("Upload response:", resp)
