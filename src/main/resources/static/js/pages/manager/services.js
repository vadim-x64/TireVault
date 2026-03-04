document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.manager-order-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const id          = this.dataset.id;
            const name        = this.dataset.name;
            const phone       = this.dataset.phone;
            const city        = this.dataset.city;
            const description = this.dataset.description;
            const status      = this.dataset.status;
            const created     = this.dataset.created;

            document.getElementById('mModalName').textContent    = name;
            document.getElementById('mModalPhone').textContent   = phone;
            document.getElementById('mModalCity').textContent    = city;
            document.getElementById('mModalCreated').textContent = created;

            const descRow = document.getElementById('mModalDescRow');
            if (description && description.trim() !== '') {
                document.getElementById('mModalDescription').textContent = description;
                descRow.classList.remove('d-none');
            } else {
                descRow.classList.add('d-none');
            }

            const badge = document.getElementById('mModalStatusBadge');
            const acceptSection   = document.getElementById('mAcceptSection');
            const completeSection = document.getElementById('mCompleteSection');
            const doneSection     = document.getElementById('mDoneSection');

            acceptSection.classList.add('d-none');
            completeSection.classList.add('d-none');
            doneSection.classList.add('d-none');
            document.getElementById('mAcceptCheck').checked = false;

            if (status === 'PENDING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-danger';
                badge.textContent = 'В очікуванні';
                document.getElementById('mAcceptForm').action = '/manager/services/' + id + '/accept';
                acceptSection.classList.remove('d-none');

            } else if (status === 'ACCEPTED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-warning text-dark';
                badge.textContent = 'Прийнято';
                document.getElementById('mCompleteForm').action = '/manager/services/' + id + '/complete';
                completeSection.classList.remove('d-none');

            } else if (status === 'COMPLETED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-success';
                badge.textContent = 'Виконано';
                doneSection.classList.remove('d-none');
            }

            const modal = new bootstrap.Modal(document.getElementById('managerOrderModal'));
            modal.show();
        });
    });
});