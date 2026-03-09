document.addEventListener('DOMContentLoaded', function () {
    function formatPhone(phone) {
        if (!phone) return phone;
        const d = phone.replace(/\D/g, '');
        if (d.length === 12 && d.startsWith('38')) {
            const p = d.substring(2);
            return `+38 (${p.substring(0, 3)})-${p.substring(3, 6)}-${p.substring(6, 8)}-${p.substring(8, 10)}`;
        }
        return phone;
    }

    document.querySelectorAll('.admin-service-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const username = this.dataset.username;
            const name = this.dataset.name;
            const phone = this.dataset.phone;
            const city = this.dataset.city;
            const description = this.dataset.description;
            const status = this.dataset.status;
            const created = this.dataset.created;
            const scheduled = this.dataset.scheduled;
            const payment = this.dataset.payment;
            document.getElementById('adminServiceUsernameLink').textContent = username || '-';
            document.getElementById('adminServiceName').textContent = name;
            document.getElementById('adminServicePhone').textContent = formatPhone(phone) || '-';
            document.getElementById('adminServiceCity').textContent = city;
            document.getElementById('adminServiceCreated').textContent = created;
            const scheduledRow = document.getElementById('adminServiceScheduledRow');
            if (scheduled && scheduled.trim() !== '') {
                document.getElementById('adminServiceScheduled').textContent = scheduled;
                scheduledRow.classList.remove('d-none');
            } else {
                scheduledRow.classList.add('d-none');
            }
            const paymentRow = document.getElementById('adminServicePaymentRow');
            if (status === 'COMPLETED' && payment) {
                const payLabel = payment === 'CARD' ? 'Карта' : 'Готівка';
                document.getElementById('adminServicePayment').textContent = payLabel;
                paymentRow.classList.remove('d-none');
            } else {
                paymentRow.classList.add('d-none');
            }
            const descRow = document.getElementById('adminServiceDescRow');
            if (description && description.trim() !== '') {
                document.getElementById('adminServiceDescription').textContent = description;
                descRow.classList.remove('d-none');
            } else {
                descRow.classList.add('d-none');
            }
            const badge = document.getElementById('adminServiceStatusBadge');
            if (status === 'PENDING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-danger';
                badge.textContent = 'В очікуванні';
            } else if (status === 'ACCEPTED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-warning text-dark';
                badge.textContent = 'Прийнято';
            } else if (status === 'SCHEDULED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-info text-dark';
                badge.textContent = 'Заброньовано: ' + scheduled;
            } else if (status === 'COMPLETED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-success';
                badge.textContent = 'Виконано';
            } else if (status === 'CANCELLED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-secondary';
                badge.textContent = 'Скасовано';
            }
            new bootstrap.Modal(document.getElementById('adminServiceModal')).show();
        });
    });
});