document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.order-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const name        = this.dataset.name;
            const phone       = this.dataset.phone;
            const city        = this.dataset.city;
            const description = this.dataset.description;
            const status      = this.dataset.status;
            const created     = this.dataset.created;

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

            const badge = document.getElementById('modalStatusBadge');
            if (status === 'PENDING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-danger';
                badge.textContent = 'В обробці';
            } else if (status === 'ACCEPTED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-warning text-dark';
                badge.textContent = 'Прийнято';
            } else if (status === 'COMPLETED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-success';
                badge.textContent = 'Виконано';
            }

            const modal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
            modal.show();
        });
    });
});