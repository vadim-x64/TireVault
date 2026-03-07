document.addEventListener("DOMContentLoaded", function () {

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

    function doRemove(itemId) {
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
    }

// Замість трьох змінних — Map для кожного товару окремо
    const pendingRemovals = new Map(); // itemId -> { timer, btn, originalHtml }

    function showUndoBar(itemId, row) {
        if (pendingRemovals.has(itemId)) return;

        const btn = row.querySelector('.remove-btn');
        const originalHtml = btn.innerHTML;
        const originalTitle = btn.title;

        const r = 8;
        const circumference = +(2 * Math.PI * r).toFixed(2);

        btn.title = 'Скасувати';
        btn.innerHTML = `
        <svg width="20" height="20" viewBox="0 0 20 20">
            <circle cx="10" cy="10" r="${r}" fill="none" stroke="#dee2e6" stroke-width="2"/>
            <circle class="progress-ring" cx="10" cy="10" r="${r}" fill="none"
                    stroke="#dc3545" stroke-width="2"
                    stroke-dasharray="${circumference}"
                    stroke-dashoffset="0"
                    stroke-linecap="round"
                    transform="rotate(-90 10 10)"/>
            <path d="M7 7l6 6M13 7l-6 6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
        </svg>`;

        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                const circle = btn.querySelector('.progress-ring');
                if (circle) {
                    circle.style.transition = 'stroke-dashoffset 5s linear';
                    circle.style.strokeDashoffset = `${circumference}`;
                }
            });
        });

        const timer = setTimeout(() => clearRemoval(itemId, true), 5000);
        pendingRemovals.set(itemId, { timer, btn, originalHtml, originalTitle });
    }

    function clearRemoval(itemId, execute) {
        const entry = pendingRemovals.get(itemId);
        if (!entry) return;

        clearTimeout(entry.timer);
        pendingRemovals.delete(itemId);

        entry.btn.innerHTML = entry.originalHtml;
        entry.btn.title = entry.originalTitle;

        if (execute) {
            doRemove(itemId);
        }
    }

    document.querySelectorAll('.remove-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const itemId = this.dataset.itemId;
            const row    = document.getElementById('row-' + itemId);
            if (!row) return;

            // Повторний клік = скасування
            if (pendingRemovals.has(itemId)) {
                clearRemoval(itemId, false);
            } else {
                showUndoBar(itemId, row);
            }
        });
    });

    function checkEmpty() {
        const tbody = document.getElementById('cart-tbody');
        if (tbody && tbody.querySelectorAll('.cart-row').length === 0) {
            location.reload();
        }
    }

    function formatPrice(val) {
        return parseFloat(val).toFixed(2) + ' ₴';
    }

    // --- CVV показати/сховати ---
    const cvvShow = document.getElementById('cvv-show');
    const cardCvv = document.getElementById('card-cvv');
    if (cvvShow && cardCvv) {
        cvvShow.addEventListener('change', function () {
            cardCvv.type = this.checked ? 'text' : 'password';
        });
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
    const stationSelect = document.getElementById('station-select');
    const checkoutBtn   = document.getElementById('checkout-btn');

    if (stationSelect) stationSelect.addEventListener('change', validateForm);

    function validateForm() {
        if (!checkoutBtn) return;
        const stationOk = stationSelect?.value.trim() !== '';
        const payMethod = document.querySelector('input[name="payMethod"]:checked');
        const payOk     = !!payMethod;
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
            const station   = stationSelect?.value || '';
            const payMethod = document.querySelector('input[name="payMethod"]:checked')?.value || '';
            fetch('/cart/checkout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `station=${encodeURIComponent(station)}&payMethod=${encodeURIComponent(payMethod)}`
            })
                .then(r => r.json())
                .then(data => {
                    if (data.success) {
                        updateCartBadge(0);
                        const confirmModal = new bootstrap.Modal(document.getElementById('confirmModal'));
                        confirmModal.show();
                        document.getElementById('confirmModal').addEventListener('hidden.bs.modal', function () {
                            window.location.href = '/';
                        }, { once: true });
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