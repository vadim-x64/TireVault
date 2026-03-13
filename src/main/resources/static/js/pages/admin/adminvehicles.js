document.getElementById('editVehicleModal').addEventListener('show.bs.modal', function (e) {
    const btn = e.relatedTarget;
    document.getElementById('editVehicleForm').action = '/admin/vehicles/' + btn.dataset.id + '/edit';
    document.getElementById('editVehicleBrand').value = btn.dataset.brand;
    document.getElementById('editVehicleModel').value = btn.dataset.model;
    document.getElementById('editVehicleYear').value = btn.dataset.year;
    document.getElementById('editVehicleModification').value = btn.dataset.modification;
});

document.getElementById('manageProductsModal').addEventListener('show.bs.modal', function (e) {
    const btn = e.relatedTarget;
    const vehicleId = btn.dataset.vehicleId;
    const label = btn.dataset.vehicleLabel;
    document.getElementById('vehicleLabel').textContent = label;
    document.getElementById('addProductForm').action = '/admin/vehicles/' + vehicleId + '/products/add';
    document.querySelectorAll('[data-vehicle-linked], [data-vehicle-empty]').forEach(el => {
        el.style.display = 'none';
    });
    const linked = document.querySelector('[data-vehicle-linked="' + vehicleId + '"]');
    const empty = document.querySelector('[data-vehicle-empty="' + vehicleId + '"]');
    if (linked) linked.style.display = 'block';
    if (empty) empty.style.display = 'block';
});