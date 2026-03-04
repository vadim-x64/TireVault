document.addEventListener("DOMContentLoaded", function () {
    const phoneInput = document.getElementById('homePhone');

    if (phoneInput) {
        phoneInput.addEventListener('input', function (e) {
            let rawPhone = e.target.value.replace(/\D/g, '').substring(0, 10);
            let formatted = '';
            if (rawPhone.length > 0) formatted = '(' + rawPhone.substring(0, 3);
            if (rawPhone.length >= 4) formatted += ')-' + rawPhone.substring(3, 6);
            if (rawPhone.length >= 7) formatted += '-' + rawPhone.substring(6, 8);
            if (rawPhone.length >= 9) formatted += '-' + rawPhone.substring(8, 10);
            e.target.value = formatted;
        });
    }
});