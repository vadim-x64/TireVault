document.getElementById('editCategoryModal').addEventListener('show.bs.modal', function (e) {
    const btn = e.relatedTarget;
    document.getElementById('editCategoryForm').action = '/admin/workservices/categories/' + btn.dataset.id + '/edit';
    document.getElementById('editCategoryName').value = btn.dataset.name;
});

document.getElementById('editServiceModal').addEventListener('show.bs.modal', function (e) {
    const btn = e.relatedTarget;
    document.getElementById('editServiceForm').action = '/admin/workservices/' + btn.dataset.id + '/edit';
    document.getElementById('editServiceName').value = btn.dataset.name;
    document.getElementById('editServiceDescription').value = btn.dataset.description || '';
    document.getElementById('editServicePrice').value = btn.dataset.price;
    document.getElementById('editServiceWorkingHours').value = btn.dataset.workingHours || '';
    const sel = document.getElementById('editServiceCategory');
    for (let opt of sel.options) {
        opt.selected = opt.value === btn.dataset.categoryId;
    }
});