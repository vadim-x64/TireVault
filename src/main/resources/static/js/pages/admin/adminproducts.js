document.getElementById('editCategoryModal').addEventListener('show.bs.modal', function (e) {
    const btn = e.relatedTarget;
    document.getElementById('editCategoryForm').action =
        '/admin/products/categories/' + btn.dataset.id + '/edit';
    document.getElementById('editCategoryName').value = btn.dataset.name;
});

document.getElementById('editProductModal').addEventListener('show.bs.modal', function (e) {
    const btn = e.relatedTarget;
    document.getElementById('editProductForm').action =
        '/admin/products/' + btn.dataset.id + '/edit';
    document.getElementById('editProductName').value        = btn.dataset.name;
    document.getElementById('editProductArticle').value     = btn.dataset.article || '';
    document.getElementById('editProductDescription').value = btn.dataset.description || '';
    document.getElementById('editProductPrice').value       = btn.dataset.price;
    document.getElementById('editProductQuantity').value    = btn.dataset.quantity;
    document.getElementById('editProductImageUrl').value    = btn.dataset.imageUrl || '';

    const sel = document.getElementById('editProductCategory');
    for (let opt of sel.options) {
        opt.selected = opt.value === btn.dataset.categoryId;
    }
});