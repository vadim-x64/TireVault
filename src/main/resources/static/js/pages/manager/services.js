document.addEventListener('DOMContentLoaded', function () {
    const WEEKDAY_HOURS = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00'];
    const SATURDAY_HOURS = ['10:00', '11:00', '12:00', '13:00', '14:00', '15:00'];

    function getWorkHours(dateStr) {
        const day = new Date(dateStr + 'T00:00:00').getDay(); // 0=Нд, 6=Сб
        if (day === 0) return null;       // неділя — вихідний
        if (day === 6) return SATURDAY_HOURS;
        return WEEKDAY_HOURS;
    }

    function formatPhone(phone) {
        if (!phone) return phone;
        const d = phone.replace(/\D/g, '');
        if (d.length === 12 && d.startsWith('38')) {
            const p = d.substring(2);
            return `+38 (${p.substring(0, 3)})-${p.substring(3, 6)}-${p.substring(6, 8)}-${p.substring(8, 10)}`;
        }
        return phone;
    }

    document.querySelectorAll('.manager-order-card').forEach(function (card) {
        card.addEventListener('click', function () {
            const id = this.dataset.id;
            const name = this.dataset.name;
            const phone = this.dataset.phone;
            const city = this.dataset.city;
            const description = this.dataset.description;
            const status = this.dataset.status;
            const created = this.dataset.created;
            const scheduled = this.dataset.scheduled;
            const payment = this.dataset.payment;

            document.getElementById('mModalName').textContent = name;
            document.getElementById('mModalPhone').textContent = formatPhone(phone);
            document.getElementById('mModalCity').textContent = city;
            document.getElementById('mModalCreated').textContent = created;

            const descRow = document.getElementById('mModalDescRow');
            if (description && description.trim() !== '') {
                document.getElementById('mModalDescription').textContent = description;
                descRow.classList.remove('d-none');
            } else {
                descRow.classList.add('d-none');
            }

            const badge = document.getElementById('mModalStatusBadge');
            const acceptSection = document.getElementById('mAcceptSection');
            const scheduleSection = document.getElementById('mScheduleSection');
            const completeSection = document.getElementById('mCompleteSection');
            const doneSection = document.getElementById('mDoneSection');
            const cancelledSection = document.getElementById('mCancelledSection');

            // Скидаємо всі секції
            [acceptSection, scheduleSection, completeSection, doneSection, cancelledSection]
                .forEach(s => s.classList.add('d-none'));
            document.getElementById('mAcceptCheck').checked = false;

            // Скидаємо форму бронювання
            document.getElementById('mScheduledAt').value = '';
            document.getElementById('mScheduleDate').value = '';
            document.getElementById('mSlotsContainer').innerHTML =
                '<p class="text-muted small mb-0">Оберіть дату вище</p>';

            if (status === 'PENDING') {
                badge.className = 'badge fs-6 px-3 py-2 bg-danger';
                badge.textContent = 'В очікуванні';
                document.getElementById('mAcceptForm').action =
                    '/manager/services/' + id + '/accept';
                acceptSection.classList.remove('d-none');

            } else if (status === 'ACCEPTED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-warning text-dark';
                badge.textContent = 'Прийнято';
                document.getElementById('mScheduleForm').action =
                    '/manager/services/' + id + '/schedule';
                document.getElementById('mCancelFromAcceptedForm').action =
                    '/manager/services/' + id + '/cancel';

                // Мінімальна дата — сьогодні
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('mScheduleDate').min = today;

                // Вішаємо обробник зміни дати (клонуємо елемент щоб прибрати старі обробники)
                const oldDateInput = document.getElementById('mScheduleDate');
                const newDateInput = oldDateInput.cloneNode(true);
                newDateInput.min = today;
                oldDateInput.parentNode.replaceChild(newDateInput, oldDateInput);

                newDateInput.addEventListener('change', function () {
                    const date = this.value;
                    if (!date) return;

                    const container = document.getElementById('mSlotsContainer');
                    document.getElementById('mScheduledAt').value = '';

                    // --- НОВА ПЕРЕВІРКА ДНЯ ---
                    const hours = getWorkHours(date);
                    if (!hours) {
                        container.innerHTML = '<p class="text-danger small mb-0">Неділя — вихідний день. Оберіть іншу дату.</p>';
                        return;
                    }
                    // --------------------------

                    container.innerHTML = '<p class="text-muted small">Завантаження слотів...</p>';

                    fetch('/api/slots?date=' + date)
                        .then(r => r.json())
                        .then(bookedHours => {
                            container.innerHTML = '';
                            hours.forEach(hour => {          // <-- тепер hours замість WORK_HOURS
                                const isBooked = bookedHours.includes(hour);
                                const btn = document.createElement('button');
                                btn.type = 'button';
                                btn.dataset.time = hour;

                                if (isBooked) {
                                    btn.className = 'btn btn-sm btn-danger me-1 mb-1 disabled';
                                    btn.textContent = hour + ' — заброньовано';
                                } else {
                                    btn.className = 'btn btn-sm btn-outline-dark me-1 mb-1 slot-btn';
                                    btn.textContent = hour;
                                    btn.addEventListener('click', function () {
                                        document.querySelectorAll('.slot-btn')
                                            .forEach(b => b.classList.remove('active', 'btn-dark'));
                                        this.classList.add('active', 'btn-dark');
                                        document.getElementById('mScheduledAt').value = date + 'T' + hour;
                                    });
                                }
                                container.appendChild(btn);
                            });
                        })
                        .catch(() => {
                            container.innerHTML = '<p class="text-danger small">Помилка завантаження слотів</p>';
                        });
                });

                scheduleSection.classList.remove('d-none');

            } else if (status === 'SCHEDULED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-info text-dark';
                badge.textContent = 'Заброньовано: ' + scheduled;
                document.getElementById('mCompleteForm').action =
                    '/manager/services/' + id + '/complete';
                document.getElementById('mUnscheduleForm').action =
                    '/manager/services/' + id + '/unschedule';
                document.getElementById('mCancelFromScheduledForm').action =
                    '/manager/services/' + id + '/cancel';
                completeSection.classList.remove('d-none');

            } else if (status === 'COMPLETED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-success';
                badge.textContent = 'Виконано';
                const payLabel = payment === 'CARD' ? 'Карта'
                    : payment === 'CASH' ? 'Готівка' : '—';
                document.getElementById('mDonePayment').textContent = payLabel;
                doneSection.classList.remove('d-none');

            } else if (status === 'CANCELLED') {
                badge.className = 'badge fs-6 px-3 py-2 bg-secondary';
                badge.textContent = 'Скасовано';
                cancelledSection.classList.remove('d-none');
            }

            const modal = new bootstrap.Modal(document.getElementById('managerOrderModal'));
            modal.show();
        });
    });
});