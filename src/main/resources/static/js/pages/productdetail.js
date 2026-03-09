document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById('qty-input');
    const minus = document.getElementById('qty-minus');
    const plus = document.getElementById('qty-plus');
    if (!input) return;
    const max = parseInt(input.max) || 999;
    minus.addEventListener('click', () => {
        const val = parseInt(input.value);
        if (val > 1) input.value = val - 1;
    });
    plus.addEventListener('click', () => {
        const val = parseInt(input.value);
        if (val < max) input.value = val + 1;
    });
    input.addEventListener('change', () => {
        let val = parseInt(input.value);
        if (isNaN(val) || val < 1) input.value = 1;
        else if (val > max) input.value = max;
    });
});