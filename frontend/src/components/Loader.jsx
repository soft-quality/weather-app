import './Loader.css';

export default function Loader() {
  return (
    <div className="loader glass" role="status" aria-live="polite">
      <span className="loader__spinner" aria-hidden="true" />
      <span>Consultando el clima…</span>
    </div>
  );
}
