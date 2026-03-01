document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('profileForm');
    const saveBtn = document.getElementById('saveProfileBtn');

    // Зберігаємо початкові значення
    const initialValues = {
        firstName: document.getElementById('profFirstName').value,
        lastName: document.getElementById('profLastName').value,
        middleName: document.getElementById('profMiddleName').value,
        phone: document.getElementById('profPhone').value
    };

    // Функція перевірки змін
    function checkChanges() {
        const currentValues = {
            firstName: document.getElementById('profFirstName').value,
            lastName: document.getElementById('profLastName').value,
            middleName: document.getElementById('profMiddleName').value,
            phone: document.getElementById('profPhone').value
        };

        let hasChanges = false;
        for (const key in initialValues) {
            if (initialValues[key] !== currentValues[key]) {
                hasChanges = true;
                break;
            }
        }

        // Розблоковуємо або блокуємо кнопку
        saveBtn.disabled = !hasChanges;
    }

    // Додаємо слухачів подій на всі інпути
    const inputs = form.querySelectorAll('input:not([disabled])');
    inputs.forEach(input => {
        input.addEventListener('input', checkChanges);
    });
});