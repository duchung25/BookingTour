/* ============================================
   HOMEPAGE INTERACTIONS
   Tropical Luxe — Traveler
   ============================================ */

document.addEventListener('DOMContentLoaded', () => {
  initStickyNavbar();
  initScrollReveal();
  initSmoothScroll();
  initCountUp();
});

/* ----- Sticky Navbar Glass Effect ----- */
function initStickyNavbar() {
  const nav = document.querySelector('.navbar-traveler');
  if (!nav) return;
  window.addEventListener('scroll', () => {
    nav.classList.toggle('scrolled', window.pageYOffset > 30);
  }, { passive: true });
}

/* ----- Scroll Reveal ----- */
function initScrollReveal() {
  const els = document.querySelectorAll('.reveal');
  if (!els.length) return;
  const io = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        e.target.classList.add('visible');
        io.unobserve(e.target);
      }
    });
  }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });
  els.forEach(el => io.observe(el));
}

/* ----- Smooth Scroll ----- */
function initSmoothScroll() {
  document.querySelectorAll('a[href^="#"]').forEach(a => {
    a.addEventListener('click', function (e) {
      const tgt = document.querySelector(this.getAttribute('href'));
      if (tgt) {
        e.preventDefault();
        window.scrollTo({
          top: tgt.getBoundingClientRect().top + window.pageYOffset - 80,
          behavior: 'smooth'
        });
      }
    });
  });
}

/* ----- Count Up Stats ----- */
function initCountUp() {
  const stats = document.querySelectorAll('[data-count]');
  if (!stats.length) return;
  const io = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        const el = e.target;
        const target = parseInt(el.dataset.count);
        const suffix = el.dataset.suffix || '';
        const dur = 2000;
        const start = performance.now();
        (function tick(now) {
          const progress = Math.min((now - start) / dur, 1);
          const eased = 1 - Math.pow(1 - progress, 3);
          el.textContent = Math.floor(eased * target) + suffix;
          if (progress < 1) requestAnimationFrame(tick);
          else el.textContent = target + suffix;
        })(start);
        io.unobserve(el);
      }
    });
  }, { threshold: 0.5 });
  stats.forEach(s => io.observe(s));
}
