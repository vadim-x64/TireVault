document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.order-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const id          = this.dataset.id;
            const name        = this.dataset.name;
            const phone       = this.dataset.phone;
            const city        = this.dataset.city;
            const description = this.dataset.description;
            const status      = this.dataset.status;
            const created     = this.dataset.created;
            const scheduled   = this.dataset.scheduled;
            const payment     = this.dataset.payment;

            document.getElementById('modalName').textContent    = name;
            document.getElementById('modalPhone').textContent   = phone;
            document.getElementById('modalCity').textContent    = city;
            document.getElementById('modalCreated').textContent = created;

            const descRow = document.getElementById('modalDescRow');
            if (description && description.trim() !== '') {
                document.getElementById('modalDescription').textContent = description;
                descRow.classList.remove('d-none');
            } else {
                descRow.classList.add('d-none');
            }

            const scheduledRow = document.getElementById('modalScheduledRow');
            if (scheduled && scheduled.trim() !== '') {
                document.getElementById('modalScheduled').textContent = scheduled;
                scheduledRow.classList.remove('d-none');
            } else {
                scheduledRow.classList.add('d-none');
            }

            // Оплата
            const paymentRow = document.getElementById('modalPaymentRow');
            if (status === 'COMPLETED' && payment) {
                const payLabel = payment === 'CARD' ? '💳 Карта' : '💵 Готівка';
                document.getElementById('modalPayment').textContent = payLabel;
                paymentRow.classList.remove('d-none');
            } else {
                paymentRow.classList.add('d-none');
            }

            const badge = document.getElementById('modalStatusBadge');
            const cancelSection = document.getElementById('modalCancelSection');
            cancelSection.classList.add('d-none');

            if (status === 'PENDING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-danger';
                badge.textContent = 'В обробці';
                document.getElementById('modalCancelForm').action = '/myservices/' + id + '/cancel';
                cancelSection.classList.remove('d-none');
            } else if (status === 'ACCEPTED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-warning text-dark';
                badge.textContent = 'Прийнято';
                document.getElementById('modalCancelForm').action = '/myservices/' + id + '/cancel';
                cancelSection.classList.remove('d-none');
            } else if (status === 'SCHEDULED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-info text-dark';
                badge.textContent = '📅 Заплановано';
                document.getElementById('modalCancelForm').action = '/myservices/' + id + '/cancel';
                cancelSection.classList.remove('d-none');
            } else if (status === 'COMPLETED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-success';
                badge.textContent = 'Виконано';
            } else if (status === 'CANCELLED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-secondary';
                badge.textContent = 'Скасовано';
            }

            const modal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
            modal.show();
        });
    });
});