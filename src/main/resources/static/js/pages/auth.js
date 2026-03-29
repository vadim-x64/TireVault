function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    if (input.type === "password") {
        input.type = "text";
    } else {
        input.type = "password";
    }
}

document.addEventListener("DOMContentLoaded", function () {
    // Форматування телефону при реєстрації
    const phoneInput = document.getElementById('regPhone');
    if (phoneInput) {
        phoneInput.addEventListener('input', function (e) {
            let x = e.target.value.replace(/\D/g, '').match(/(\d{0,3})(\d{0,3})(\d{0,2})(\d{0,2})/);
            if (!x[2]) {
                e.target.value = x[1] === '' ? '' : '(' + x[1];
            } else {
                e.target.value = '(' + x[1] + ')-' + x[2] + (x[3] ? '-' + x[3] : '') + (x[4] ? '-' + x[4] : '');
            }
        });
    }

    // ← НОВЕ: логіка модалки "Забули пароль?"
    const forgotModal = document.getElementById('forgotPasswordModal');
    if (forgotModal) {
        // Скидаємо стан при закритті
        forgotModal.addEventListener('hidden.bs.modal', function () {
            document.getElementById('forgotStep1').classList.remove('d-none');
            document.getElementById('forgotStep2').classList.add('d-none');
            document.getElementById('forgotEmail').value = '';
            document.getElementById('forgotNewPassword').value = '';
            document.getElementById('forgotEmailError').classList.add('d-none');
            document.getElementById('forgotResetError').classList.add('d-none');
        });
    }

    // Крок 1: перевірка email
    const forgotCheckBtn = document.getElementById('forgotCheckBtn');
    if (forgotCheckBtn) {
        forgotCheckBtn.addEventListener('click', async function () {
            const email = document.getElementById('forgotEmail').value.trim();
            const errorDiv = document.getElementById('forgotEmailError');

            if (!email) {
                errorDiv.textContent = 'Введіть електронну пошту.';
                errorDiv.classList.remove('d-none');
                return;
            }

            forgotCheckBtn.disabled = true;
            forgotCheckBtn.textContent = 'Перевірка...';

            try {
                const formData = new FormData();
                formData.append('email', email);
                const resp = await fetch('/api/auth/forgot-password/check', {
                    method: 'POST',
                    body: formData
                });
                const data = await resp.json();

                if (resp.ok) {
                    // Переходимо до кроку 2
                    document.getElementById('forgotStep1').classList.add('d-none');
                    document.getElementById('forgotStep2').classList.remove('d-none');
                } else {
                    errorDiv.textContent = data.error || 'Помилка.';
                    errorDiv.classList.remove('d-none');
                }
            } catch (e) {
                errorDiv.textContent = 'Помилка з\'єднання.';
                errorDiv.classList.remove('d-none');
            } finally {
                forgotCheckBtn.disabled = false;
                forgotCheckBtn.textContent = 'Перевірити';
            }
        });
    }

    // Крок 2: встановити новий пароль
    const forgotResetBtn = document.getElementById('forgotResetBtn');
    if (forgotResetBtn) {
        forgotResetBtn.addEventListener('click', async function () {
            const email = document.getElementById('forgotEmail').value.trim();
            const newPassword = document.getElementById('forgotNewPassword').value;
            const errorDiv = document.getElementById('forgotResetError');

            if (newPassword.length < 8) {
                errorDiv.textContent = 'Пароль має містити мінімум 8 символів.';
                errorDiv.classList.remove('d-none');
                return;
            }

            forgotResetBtn.disabled = true;
            forgotResetBtn.textContent = 'Збереження...';

            try {
                const formData = new FormData();
                formData.append('email', email);
                formData.append('newPassword', newPassword);
                const resp = await fetch('/api/auth/forgot-password/reset', {
                    method: 'POST',
                    body: formData
                });
                const data = await resp.json();

                if (resp.ok) {
                    // Закриваємо модалку і показуємо успіх на сторінці входу
                    const modal = bootstrap.Modal.getInstance(document.getElementById('forgotPasswordModal'));
                    modal.hide();
                    // Показуємо повідомлення про успіх
                    const loginTab = document.getElementById('login');
                    const successAlert = document.createElement('div');
                    successAlert.className = 'alert alert-success';
                    successAlert.textContent = 'Пароль успішно змінено. Тепер ви можете увійти.';
                    loginTab.insertBefore(successAlert, loginTab.firstChild);
                } else {
                    errorDiv.textContent = data.error || 'Помилка.';
                    errorDiv.classList.remove('d-none');
                }
            } catch (e) {
                errorDiv.textContent = 'Помилка з\'єднання.';
                errorDiv.classList.remove('d-none');
            } finally {
                forgotResetBtn.disabled = false;
                forgotResetBtn.textContent = 'Змінити пароль';
            }
        });
    }
});