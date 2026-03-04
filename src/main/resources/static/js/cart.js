document.addEventListener("DOMContentLoaded", function () {

    function updateCartBadge(count) {
        const badge = document.getElementById('cart-badge');
        if (!badge) return;
        if (count > 0) {
            badge.textContent = count;
            badge.style.display = 'inline-block';
        } else {
            badge.style.display = 'none';
        }
    }

    document.querySelectorAll('.btn-add-to-cart').forEach(btn => {
        btn.addEventListener('click', function () {
            const productId = this.dataset.productId;
            const qtyInput  = document.getElementById('qty-input');
            const quantity  = qtyInput ? parseInt(qtyInput.value) || 1 : 1;

            const originalHtml = this.innerHTML;
            this.disabled = true;
            this.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Додаємо...';

            fetch('/cart/add', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `productId=${productId}&quantity=${quantity}`
            })
                .then(r => r.json())
                .then(data => {
                    if (data.success) {
                        updateCartBadge(data.cartCount);
                        this.innerHTML = '✓ Додано!';
                        this.classList.remove('btn-dark');
                        this.classList.add('btn-success');
                        setTimeout(() => {
                            this.innerHTML = originalHtml;
                            this.classList.remove('btn-success');
                            this.classList.add('btn-dark');
                            this.disabled = false;
                        }, 2000);
                    } else {
                        alert(data.message || 'Помилка. Спробуйте ще раз.');
                        this.innerHTML = originalHtml;
                        this.disabled = false;
                    }
                })
                .catch(() => {
                    alert('Помилка з\'єднання.');
                    this.innerHTML = originalHtml;
                    this.disabled = false;
                });
        });
    });
});