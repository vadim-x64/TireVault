document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('profileForm');
    const saveBtn = document.getElementById('saveProfileBtn');
    const phoneInput = document.getElementById('profPhone');

    // Надійна функція форматування без regex-груп (щоб уникнути багів з комами)
    function formatPhone(value) {
        // 1. Залишаємо тільки цифри
        let rawPhone = value.replace(/\D/g, '');

        // 2. Якщо номер підтягнувся з бази з кодом 38 на початку, відсікаємо його
        if (rawPhone.startsWith('38') && rawPhone.length > 10) {
            rawPhone = rawPhone.substring(2);
        }

        // 3. Обмежуємо довжину максимум 10 цифрами
        rawPhone = rawPhone.substring(0, 10);

        // 4. Формуємо маску (0XX)-XXX-XX-XX
        let formatted = '';
        if (rawPhone.length > 0) {
            formatted = '(' + rawPhone.substring(0, 3);
        }
        if (rawPhone.length >= 4) {
            formatted += ')-' + rawPhone.substring(3, 6);
        }
        if (rawPhone.length >= 7) {
            formatted += '-' + rawPhone.substring(6, 8);
        }
        if (rawPhone.length >= 9) {
            formatted += '-' + rawPhone.substring(8, 10);
        }

        return formatted;
    }

    // Відразу форматуємо існуючий номер при завантаженні сторінки
    if (phoneInput && phoneInput.value) {
        phoneInput.value = formatPhone(phoneInput.value);
    }

    // Зберігаємо початкові значення для перевірки змін
    const initialValues = {
        firstName: document.getElementById('profFirstName').value,
        lastName: document.getElementById('profLastName').value,
        middleName: document.getElementById('profMiddleName').value,
        phone: phoneInput.value
    };

    // Функція розблокування кнопки "Зберегти"
    function checkChanges() {
        const currentValues = {
            firstName: document.getElementById('profFirstName').value,
            lastName: document.getElementById('profLastName').value,
            middleName: document.getElementById('profMiddleName').value,
            phone: phoneInput.value
        };

        let hasChanges = false;
        for (const key in initialValues) {
            if (initialValues[key] !== currentValues[key]) {
                hasChanges = true;
                break;
            }
        }

        saveBtn.disabled = !hasChanges;
    }

    // Слухаємо ввід в поля
    const inputs = form.querySelectorAll('input:not([disabled])');
    inputs.forEach(input => {
        input.addEventListener('input', function(e) {
            if (e.target.id === 'profPhone') {
                // Зберігаємо позицію курсора, щоб він не стрибав у кінець при редагуванні посередині
                let cursorPosition = e.target.selectionStart;
                let oldLength = e.target.value.length;

                e.target.value = formatPhone(e.target.value);

                // Коригуємо позицію курсора після переформатування
                let newLength = e.target.value.length;
                cursorPosition = cursorPosition + (newLength - oldLength);
                e.target.setSelectionRange(cursorPosition, cursorPosition);
            }
            checkChanges();
        });
    });
});