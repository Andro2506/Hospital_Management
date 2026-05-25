/* Client-side validation helpers for the Hospital Management System.
 * Mirrors com.hospital.util.ValidationUtil server-side rules so users get
 * fast feedback before submission. The server is the source of truth.
 */
(function () {
    'use strict';

    var rules = {
        userId:    /^[A-Za-z0-9]{8,}$/,
        password:  /^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{10,}$/,
        patientId: /^\d{7}$/,
        contactNo: /^\d{10}$/,
        aadhar:    /^\d{12}$/,
        email:     /^[^@\s]+@[^@\s]+\.[^@\s]+$/
    };

    function setError(input, message) {
        clearError(input);
        var help = document.createElement('div');
        help.className = 'help';
        help.style.color = '#962e23';
        help.textContent = message;
        help.dataset.errorFor = input.id || input.name;
        input.parentNode.appendChild(help);
        input.style.borderColor = '#c0392b';
    }

    function clearError(input) {
        var helps = input.parentNode.querySelectorAll('.help');
        helps.forEach(function (h) {
            if (h.dataset && h.dataset.errorFor) h.parentNode.removeChild(h);
        });
        input.style.borderColor = '';
    }

    function isBlank(v) { return !v || !v.trim(); }

    function validate(form) {
        var ok = true;

        // Generic data-validate attribute pattern: "rule:Message"
        var fields = form.querySelectorAll('[data-validate]');
        fields.forEach(function (input) {
            var spec = input.getAttribute('data-validate');
            if (!spec) return;
            var parts = spec.split('|');
            for (var i = 0; i < parts.length; i++) {
                var p = parts[i].split(':');
                var ruleName = p[0];
                var msg = p[1] || 'Invalid value';
                var v = input.value || '';

                if (ruleName === 'required' && isBlank(v)) { setError(input, msg); ok = false; return; }
                if (ruleName === 'email'    && !rules.email.matcher && !rules.email.test(v)) { setError(input, msg); ok = false; return; }
                if (ruleName === 'userId'   && !rules.userId.test(v))   { setError(input, msg); ok = false; return; }
                if (ruleName === 'password' && !rules.password.test(v)) { setError(input, msg); ok = false; return; }
                if (ruleName === 'patientId'&& !rules.patientId.test(v)){ setError(input, msg); ok = false; return; }
                if (ruleName === 'contact'  && !rules.contactNo.test(v)){ setError(input, msg); ok = false; return; }
                if (ruleName === 'aadhar'   && !rules.aadhar.test(v))   { setError(input, msg); ok = false; return; }
                if (ruleName === 'maxlen') {
                    var max = parseInt(p[1], 10);
                    var msg2 = p[2] || ('Must be at most ' + max + ' characters');
                    if (v.length > max) { setError(input, msg2); ok = false; return; }
                }
            }
            clearError(input);
        });

        // Confirm-password: data-match="<id-of-other-field>"
        var matchFields = form.querySelectorAll('[data-match]');
        matchFields.forEach(function (input) {
            var otherId = input.getAttribute('data-match');
            var msg = input.getAttribute('data-match-message') || 'Values do not match';
            var other = document.getElementById(otherId);
            if (other && input.value !== other.value) {
                setError(input, msg);
                ok = false;
            } else {
                clearError(input);
            }
        });
        return ok;
    }

    document.addEventListener('DOMContentLoaded', function () {
        var forms = document.querySelectorAll('form[data-validate-form]');
        forms.forEach(function (form) {
            form.addEventListener('submit', function (ev) {
                if (!validate(form)) {
                    ev.preventDefault();
                    var firstErr = form.querySelector('[style*="border-color"]');
                    if (firstErr) firstErr.focus();
                }
            });
        });

        // Confirm dialog helpers (data-confirm="message")
        document.querySelectorAll('[data-confirm]').forEach(function (el) {
            el.addEventListener('click', function (ev) {
                var msg = el.getAttribute('data-confirm');
                if (!confirm(msg)) ev.preventDefault();
            });
        });
    });
})();
