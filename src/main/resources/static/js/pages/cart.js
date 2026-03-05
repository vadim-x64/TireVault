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
    const pendingRemovals = new Map(); // itemId -> { timer, row, intervalId, barEl, cdEl }

    function showUndoBar(itemId, row) {
        // Якщо цей товар вже в черзі — ігноруємо
        if (pendingRemovals.has(itemId)) return;

        row.style.opacity = '0.35';
        row.style.pointerEvents = 'none';

        // Створюємо окремий undo-bar для кожного товару
        const bar = document.createElement('div');
        bar.className = 'position-fixed translate-middle-x bg-dark text-white rounded shadow-lg px-3 py-2 d-flex align-items-center gap-3';
        bar.style.cssText = `bottom:${20 + pendingRemovals.size * 60}px; left:50%; transform:translateX(-50%); z-index:1055; min-width:300px;`;
        bar.innerHTML = `<span class="small">Видалення через <strong class="countdown">5</strong> с...</span>
                     <button type="button" class="btn btn-sm btn-outline-light ms-auto undo-btn-inner">↩ Скасувати</button>`;
        document.body.appendChild(bar);

        const cd = bar.querySelector('.countdown');
        let seconds = 5;

        const intervalId = setInterval(() => {
            seconds--;
            cd.textContent = seconds;
            if (seconds <= 0) {
                clearRemoval(itemId, true);
            }
        }, 1000);

        const entry = { row, intervalId, bar };
        pendingRemovals.set(itemId, entry);

        bar.querySelector('.undo-btn-inner').addEventListener('click', () => {
            clearRemoval(itemId, false);
        });
    }

    function clearRemoval(itemId, execute) {
        const entry = pendingRemovals.get(itemId);
        if (!entry) return;

        clearInterval(entry.intervalId);
        entry.bar.remove();
        pendingRemovals.delete(itemId);

        if (execute) {
            doRemove(itemId);
        } else {
            entry.row.style.opacity = '';
            entry.row.style.pointerEvents = '';
        }
    }

    document.querySelectorAll('.remove-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const itemId = this.dataset.itemId;
            const row    = document.getElementById('row-' + itemId);
            if (!row) return;
            showUndoBar(itemId, row);
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
        const stationOk = stationSelect?.value !== '';
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