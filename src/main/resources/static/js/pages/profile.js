document.addEventListener('DOMContentLoaded', function() {
    const profileForm = document.getElementById('profileForm');
    const saveProfileBtn = document.getElementById('saveProfileBtn');
    const phoneInput = document.getElementById('profPhone');

    function formatPhone(value) {
        let rawPhone = value.replace(/\D/g, '');
        if (rawPhone.startsWith('38') && rawPhone.length > 10) {
            rawPhone = rawPhone.substring(2);
        }
        rawPhone = rawPhone.substring(0, 10);
        let formatted = '';
        if (rawPhone.length > 0) formatted = '(' + rawPhone.substring(0, 3);
        if (rawPhone.length >= 4) formatted += ')-' + rawPhone.substring(3, 6);
        if (rawPhone.length >= 7) formatted += '-' + rawPhone.substring(6, 8);
        if (rawPhone.length >= 9) formatted += '-' + rawPhone.substring(8, 10);
        return formatted;
    }

    if (phoneInput && phoneInput.value) {
        phoneInput.value = formatPhone(phoneInput.value);
    }

    const initialProfileValues = {
        firstName: document.getElementById('profFirstName')?.value || '',
        lastName: document.getElementById('profLastName')?.value || '',
        middleName: document.getElementById('profMiddleName')?.value || '',
        phone: phoneInput ? phoneInput.value : ''
    };

    function checkProfileChanges() {
        if (!saveProfileBtn) return;

        const currentValues = {
            firstName: document.getElementById('profFirstName').value,
            lastName: document.getElementById('profLastName').value,
            middleName: document.getElementById('profMiddleName').value,
            phone: phoneInput.value
        };

        let hasChanges = false;
        for (const key in initialProfileValues) {
            if (initialProfileValues[key] !== currentValues[key]) {
                hasChanges = true;
                break;
            }
        }
        saveProfileBtn.disabled = !hasChanges;
    }

    if (profileForm) {
        const inputs = profileForm.querySelectorAll('input:not([disabled])');
        inputs.forEach(input => {
            input.addEventListener('input', function(e) {
                if (e.target.id === 'profPhone') {
                    let cursorPosition = e.target.selectionStart;
                    let oldLength = e.target.value.length;
                    e.target.value = formatPhone(e.target.value);
                    let newLength = e.target.value.length;
                    cursorPosition = cursorPosition + (newLength - oldLength);
                    e.target.setSelectionRange(cursorPosition, cursorPosition);
                }
                checkProfileChanges();
            });
        });
    }

    const securityForm = document.getElementById('securityForm');
    const saveSecurityBtn = document.getElementById('saveSecurityBtn');
    const secUsernameInput = document.getElementById('secUsername');
    const secPasswordInput = document.getElementById('secPassword');

    if (secPasswordInput) {
        secPasswordInput.value = '';
    }

    const initialSecurityValues = {
        username: secUsernameInput ? secUsernameInput.value : ''
    };

    function checkSecurityChanges() {
        if (!saveSecurityBtn || !secUsernameInput || !secPasswordInput) return;

        const currentUsername = secUsernameInput.value;
        const currentPassword = secPasswordInput.value;
        let hasChanges = (currentUsername !== initialSecurityValues.username && currentUsername.trim() !== '') || (currentPassword.length > 0);

        saveSecurityBtn.disabled = !hasChanges;
    }

    if (securityForm) {
        checkSecurityChanges();

        const secInputs = securityForm.querySelectorAll('input:not([type="checkbox"])');
        secInputs.forEach(input => {
            input.addEventListener('input', checkSecurityChanges);
        });

        setTimeout(() => {
            if (secPasswordInput && secPasswordInput.value !== '') {
                secPasswordInput.value = '';
                checkSecurityChanges();
            }
        }, 100);
    }

    const deleteErrorAlert = document.getElementById('deleteAccountErrorAlert');
    if (deleteErrorAlert && typeof bootstrap !== 'undefined') {
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteAccountModal'));
        deleteModal.show();
    }
});

function toggleProfilePassword() {
    const pwdInput = document.getElementById('secPassword');
    if (pwdInput) {
        if (pwdInput.type === "password") {
            pwdInput.type = "text";
        } else {
            pwdInput.type = "password";
        }
    }
}

function toggleDeleteModalPassword() {
    const pwdInput = document.getElementById('deletePassword');
    if (pwdInput) {
        if (pwdInput.type === "password") {
            pwdInput.type = "text";
        } else {
            pwdInput.type = "password";
        }
    }
}