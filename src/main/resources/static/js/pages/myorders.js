document.addEventListener('DOMContentLoaded', function () {

    document.querySelectorAll('.myorder-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const id        = this.dataset.id;
            const status    = this.dataset.status;
            const created   = this.dataset.created;
            const total     = parseFloat(this.dataset.total).toFixed(2);
            const station   = this.dataset.station;
            const payMethod = this.dataset.paymethod;
            const items     = JSON.parse(this.dataset.items || '[]');

            document.getElementById('myOrderCreated').textContent = created;
            document.getElementById('myOrderStation').textContent = station || '—';
            document.getElementById('myOrderTotal').textContent   = total + ' ₴';

            const payLabel = payMethod === 'card' ? '💳 Карткою'
                : payMethod === 'cash' ? '💵 Готівка на СТО' : '—';
            document.getElementById('myOrderPayMethod').textContent = payLabel;

            // Позиції
            const tbody = document.getElementById('myOrderItems');
            tbody.innerHTML = '';
            items.forEach(it => {
                const tr = document.createElement('tr');
                tr.innerHTML = `<td>${it.name}</td>
                                <td class="text-center">${it.qty}</td>
                                <td class="text-end">${parseFloat(it.price).toFixed(2)} ₴</td>
                                <td class="text-end">${parseFloat(it.subtotal).toFixed(2)} ₴</td>`;
                tbody.appendChild(tr);
            });

            // Статус + кнопка скасування
            const badge = document.getElementById('myOrderStatusBadge');
            const cancelSection = document.getElementById('myOrderCancelSection');
            cancelSection.classList.add('d-none');

            if (status === 'PENDING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-danger';
                badge.textContent = 'В обробці';
                document.getElementById('myOrderCancelForm').action = '/myorders/' + id + '/cancel';
                cancelSection.classList.remove('d-none');
            } else if (status === 'PROCESSING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-warning text-dark';
                badge.textContent = 'Виконується';
                document.getElementById('myOrderCancelForm').action = '/myorders/' + id + '/cancel';
                cancelSection.classList.remove('d-none');
            } else if (status === 'COMPLETED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-success';
                badge.textContent = 'Виконано';
            } else if (status === 'CANCELLED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-secondary';
                badge.textContent = 'Скасовано';
            }

            new bootstrap.Modal(document.getElementById('myOrderModal')).show();
        });
    });
});