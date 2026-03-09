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

    document.querySelectorAll('.mgr-order-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const id        = this.dataset.id;
            const username  = this.dataset.username;
            const fullname  = this.dataset.fullname;
            const phone     = this.dataset.phone;
            const status    = this.dataset.status;
            const created   = this.dataset.created;
            const total     = parseFloat(this.dataset.total).toFixed(2);
            const station   = this.dataset.station;
            const payMethod = this.dataset.paymethod;
            const items     = JSON.parse(this.dataset.items || '[]');

            document.getElementById('mgrOrderUsername').textContent = username;
            document.getElementById('mgrOrderFullname').textContent  = fullname || '-';
            document.getElementById('mgrOrderPhone').textContent     = formatPhone(phone) || '-';
            document.getElementById('mgrOrderCreated').textContent  = created;
            document.getElementById('mgrOrderStation').textContent   = station || '-';
            document.getElementById('mgrOrderTotal').textContent     = total + ' ₴';

            const payLabel = payMethod === 'card' ? 'Карткою'
                : payMethod === 'cash' ? 'Готівка на СТО' : '-';
            document.getElementById('mgrOrderPayMethod').textContent = payLabel;

            // Позиції
            const tbody = document.getElementById('mgrOrderItems');
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
            const badge = document.getElementById('mgrOrderStatusBadge');
            const pendingSection    = document.getElementById('mgrPendingSection');
            const processingSection = document.getElementById('mgrProcessingSection');
            const doneSection       = document.getElementById('mgrDoneSection');

            [pendingSection, processingSection, doneSection].forEach(s => s.classList.add('d-none'));

            const statusBase = '/manager/orders/' + id + '/status';
            const cancelUrl  = '/manager/orders/' + id + '/cancel';

            if (status === 'PENDING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-danger';
                badge.textContent = 'Нове';
                document.getElementById('mgrProcessingForm').action              = statusBase;
                document.getElementById('mgrCompleteFromPendingForm').action     = statusBase;
                document.getElementById('mgrCancelFromPendingForm').action       = cancelUrl;
                pendingSection.classList.remove('d-none');

            } else if (status === 'PROCESSING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-warning text-dark';
                badge.textContent = 'Виконується';
                document.getElementById('mgrCompleteFromProcessingForm').action  = statusBase;
                document.getElementById('mgrCancelFromProcessingForm').action    = cancelUrl;
                processingSection.classList.remove('d-none');

            } else if (status === 'COMPLETED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-success';
                badge.textContent = 'Виконано';
                doneSection.classList.remove('d-none');

            } else if (status === 'CANCELLED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-secondary';
                badge.textContent = 'Скасовано';
                doneSection.classList.remove('d-none');
            }

            new bootstrap.Modal(document.getElementById('mgrOrderModal')).show();
        });
    });
});