// Currency, number, and text formatting

export function formatINR(amount) {
  if (amount == null || isNaN(amount)) return '0.00';
  const num = typeof amount === 'number' ? amount : parseFloat(amount);
  return new Intl.NumberFormat('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(num);
}

export function formatReference(ref) {
  if (!ref) return '';
  return ref.toUpperCase();
}

