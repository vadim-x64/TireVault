document.addEventListener('DOMContentLoaded', function () {

    function formatPhone(phone) {
        if (!phone) return phone;
        const d = phone.replace(/\D/g, '');
        if (d.length === 12 && d.startsWith('38')) {
            const p = d.substring(2);
            return `+38 (${p.substring(0,3)})-${p.substring(3,6)}-${p.substring(6,8)}-${p.substring(8,10)}`;
        }
        return phone;
    }

    document.querySelectorAll('.admin-order-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const username  = this.dataset.username;
            const fullname  = this.dataset.fullname;
            const phone     = this.dataset.phone;
            const status    = this.dataset.status;
            const created   = this.dataset.created;
            const total     = parseFloat(this.dataset.total).toFixed(2);
            const station   = this.dataset.station;
            const payMethod = this.dataset.paymethod;
            const items     = JSON.parse(this.dataset.items || '[]');

            document.getElementById('adminOrderUsernameLink').textContent = username;
            document.getElementById('adminOrderFullname').textContent     = fullname || '—';
            document.getElementById('adminOrderPhone').textContent        = formatPhone(phone) || '—';
            document.getElementById('adminOrderCreated').textContent      = created;
            document.getElementById('adminOrderStation').textContent      = station || '—';
            document.getElementById('adminOrderTotal').textContent        = total + ' ₴';

            const payLabel = payMethod === 'card' ? '💳 Карткою'
                : payMethod === 'cash' ? '💵 Готівка на СТО' : '—';
            document.getElementById('adminOrderPayMethod').textContent = payLabel;

            // Позиції
            const tbody = document.getElementById('adminOrderItems');
            tbody.innerHTML = '';
            items.forEach(it => {
                const tr = document.createElement('tr');
                tr.innerHTML = `<td>${it.name}</td>
                                <td class="text-center">${it.qty}</td>
                                <td class="text-end">${parseFloat(it.price).toFixed(2)} ₴</td>
                                <td class="text-end">${parseFloat(it.subtotal).toFixed(2)} ₴</td>`;
                tbody.appendChild(tr);
            });

            // Статус-бейдж
            const badge = document.getElementById('adminOrderStatusBadge');
            if (status === 'PENDING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-danger';
                badge.textContent = 'Нове';
            } else if (status === 'PROCESSING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-warning text-dark';
                badge.textContent = 'Виконується';
            } else if (status === 'COMPLETED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-success';
                badge.textContent = 'Виконано';
            } else if (status === 'CANCELLED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-secondary';
                badge.textContent = 'Скасовано';
            }

            new bootstrap.Modal(document.getElementById('adminOrderModal')).show();
        });
    });
});