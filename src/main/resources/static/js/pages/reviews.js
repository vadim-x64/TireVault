document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.btn-like').forEach(btn => {
        btn.addEventListener('click', function () {
            const id = this.dataset.reviewId;
            fetch(`/reviews/${id}/like`, { method: 'POST' })
                .then(r => r.json())
                .then(data => {
                    if (data.error) return;
                    const icon = this.querySelector('i');
                    const count = this.querySelector('.like-count');
                    if (data.liked) {
                        icon.className = 'bi bi-heart-fill';
                        this.classList.replace('text-secondary', 'text-danger');
                    } else {
                        icon.className = 'bi bi-heart';
                        this.classList.replace('text-danger', 'text-secondary');
                    }
                    count.textContent = data.count;
                });
        });
    });

    document.querySelectorAll('.btn-reply').forEach(btn => {
        btn.addEventListener('click', function () {
            const parentId      = this.dataset.reviewId;
            const username      = this.dataset.username;
            const userId        = this.dataset.userId;
            const replyToReviewId = this.dataset.replyToReviewId || parentId;
            const targetType    = this.dataset.targetType;
            const targetId      = this.dataset.targetId;

            document.querySelectorAll('.reply-form').forEach(f => f.classList.add('d-none'));

            const formBlock = document.getElementById('reply-form-' + parentId);
            if (!formBlock) return;
            formBlock.classList.remove('d-none');

            const form = formBlock.querySelector('form');
            form.querySelector('[name="replyToReviewId"]').value = replyToReviewId;
            form.querySelector('[name="replyToUserId"]').value   = userId;

            const textarea = form.querySelector('textarea');
            textarea.value = '@' + username + ' ';
            textarea.focus();
            textarea.setSelectionRange(textarea.value.length, textarea.value.length);

            highlightReview(replyToReviewId);
        });
    });

    document.querySelectorAll('.btn-cancel-reply').forEach(btn => {
        btn.addEventListener('click', function () {
            const id = this.dataset.reviewId;
            const form = document.getElementById('reply-form-' + id);
            if (form) form.classList.add('d-none');
        });
    });

    document.querySelectorAll('.reply-form form').forEach(form => {
        form.addEventListener('submit', function (e) {
            const textarea = this.querySelector('textarea');
            const stripped = textarea.value.trim().replace(/^@\S+\s*/, '').trim();
            if (!stripped) {
                e.preventDefault();
                textarea.classList.add('is-invalid');
                textarea.focus();
            } else {
                textarea.classList.remove('is-invalid');
            }
        });
    });

    document.querySelectorAll('.btn-toggle-replies').forEach(btn => {
        btn.addEventListener('click', function () {
            const id = this.dataset.reviewId;
            const container = document.getElementById('replies-' + id);
            if (!container) return;
            const hidden = container.classList.toggle('d-none');
            const icon   = this.querySelector('i');
            const label  = this.querySelector('span');
            icon.className = hidden ? 'bi bi-chevron-down me-1' : 'bi bi-chevron-up me-1';
            const count = container.querySelectorAll('.reply-card').length;
            label.textContent = (hidden ? 'Відповіді' : 'Сховати') + ' (' + count + ')';
        });
    });

    document.querySelectorAll('.reply-mention').forEach(span => {
        span.addEventListener('click', function () {
            const refId = this.dataset.refId;
            if (refId) highlightReview(refId);
        });
    });

    const hash = window.location.hash;
    if (hash && hash.startsWith('#review-')) {
        const id = hash.replace('#review-', '');
        highlightReview(id, false);
    }

    function highlightReview(id, scroll = true) {
        document.querySelectorAll('.review-card').forEach(c =>
            c.classList.remove('review-highlight'));
        const el = document.getElementById('review-' + id);
        if (!el) return;
        const repliesBlock = el.closest('.replies-container');
        if (repliesBlock) {
            repliesBlock.classList.remove('d-none');
            const pid = repliesBlock.id.replace('replies-', '');
            const toggleBtn = document.querySelector(
                '.btn-toggle-replies[data-review-id="' + pid + '"]');
            if (toggleBtn) {
                const icon  = toggleBtn.querySelector('i');
                const label = toggleBtn.querySelector('span');
                icon.className = 'bi bi-chevron-up me-1';
                const count = repliesBlock.querySelectorAll('.reply-card').length;
                label.textContent = 'Сховати (' + count + ')';
            }
        }
        el.classList.add('review-highlight');
        if (scroll) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        setTimeout(() => el.classList.remove('review-highlight'), 2500);
    }
});