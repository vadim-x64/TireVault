document.addEventListener("DOMContentLoaded", function () {

    // --- Оновлення бейджу кошика ---
    function updateCartBadge(count) {
        const badge = document.getElementById('cart-badge');
        if (!badge) return;
        badge.textContent = count;
        badge.style.display = count > 0 ? 'inline-block' : 'none';
    }

    // --- Кнопки кількості ---
    document.querySelectorAll('.qty-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const itemId = this.dataset.itemId;
            const delta  = parseInt(this.dataset.delta);
            const qtyEl  = document.getElementById('qty-' + itemId);
            const newQty = Math.max(0, parseInt(qtyEl.textContent) + delta);

            fetch('/cart/update', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `itemId=${itemId}&quantity=${newQty}`
            })
                .then(r => r.json())
                .then(data => {
                    if (!data.success) return;

                    if (data.removed) {
                        document.getElementById('row-' + itemId)?.remove();
                        checkEmpty();
                    } else {
                        qtyEl.textContent = newQty;
                        document.getElementById('subtotal-' + itemId).textContent =
                            formatPrice(data.subtotal);
                    }
                    document.getElementById('cart-total').textContent = formatPrice(data.total);
                    updateCartBadge(data.cartCount);
                });
        });
    });

    // --- Видалення товару ---
    document.querySelectorAll('.remove-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const itemId = this.dataset.itemId;

            fetch('/cart/remove', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `itemId=${itemId}`
            })
                .then(r => r.json())
                .then(data => {
                    if (!data.success) return;
                    document.getElementById('row-' + itemId)?.remove();
                    document.getElementById('cart-total').textContent = formatPrice(data.total);
                    updateCartBadge(data.cartCount);
                    if (data.empty) checkEmpty();
                });
        });
    });

    function checkEmpty() {
        const tbody = document.getElementById('cart-tbody');
        if (tbody && tbody.querySelectorAll('.cart-row').length === 0) {
            location.reload(); // перезавантажуємо щоб показати "кошик порожній"
        }
    }

    function formatPrice(val) {
        return parseFloat(val).toFixed(2) + ' ₴';
    }

    // --- Спосіб оплати ---
    const cardFormBlock = document.getElementById('card-form-block');
    document.querySelectorAll('input[name="payMethod"]').forEach(radio => {
        radio.addEventListener('change', function () {
            if (cardFormBlock) {
                cardFormBlock.style.display = this.value === 'card' ? 'block' : 'none';
            }
            validateForm();
        });
    });

    // --- Форматування картки ---
    const cardNumber = document.getElementById('card-number');
    const cardExpiry = document.getElementById('card-expiry');
    const cardCvv    = document.getElementById('card-cvv');

    if (cardNumber) {
        cardNumber.addEventListener('input', function () {
            let val = this.value.replace(/\D/g, '').substring(0, 16);
            this.value = val.match(/.{1,4}/g)?.join(' ') || val;
            validateForm();
        });
    }
    if (cardExpiry) {
        cardExpiry.addEventListener('input', function () {
            let val = this.value.replace(/\D/g, '').substring(0, 4);
            if (val.length >= 3) val = val.substring(0, 2) + '/' + val.substring(2);
            this.value = val;
            validateForm();
        });
    }
    if (cardCvv) {
        cardCvv.addEventListener('input', function () {
            this.value = this.value.replace(/\D/g, '').substring(0, 3);
            validateForm();
        });
    }

    // --- Валідація форми ---
    const stationSelect  = document.getElementById('station-select');
    const checkoutBtn    = document.getElementById('checkout-btn');

    if (stationSelect) stationSelect.addEventListener('change', validateForm);

    function validateForm() {
        if (!checkoutBtn) return;

        const stationOk  = stationSelect?.value !== '';
        const payMethod  = document.querySelector('input[name="payMethod"]:checked');
        const payOk      = !!payMethod;

        let cardOk = true;
        if (payMethod?.value === 'card') {
            cardOk = cardNumber?.value.replace(/\s/g, '').length === 16 &&
                cardExpiry?.value.length === 5 &&
                cardCvv?.value.length === 3;
        }

        checkoutBtn.disabled = !(stationOk && payOk && cardOk);
    }

    // --- Оформити замовлення ---
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', function () {
            checkoutBtn.disabled = true;
            checkoutBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Обробка...';

            fetch('/cart/checkout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            })
                .then(r => r.json())
                .then(data => {
                    if (data.success) {
                        updateCartBadge(0);
                        const modal = new bootstrap.Modal(document.getElementById('confirmModal'));
                        modal.show();
                    } else {
                        alert(data.message || 'Помилка оформлення. Спробуйте ще раз.');
                        checkoutBtn.disabled = false;
                        checkoutBtn.innerHTML = 'Оформити замовлення';
                    }
                })
                .catch(() => {
                    alert('Помилка з\'єднання.');
                    checkoutBtn.disabled = false;
                    checkoutBtn.innerHTML = 'Оформити замовлення';
                });
        });
    }
});