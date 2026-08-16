import './ErrorMessage.css';

export default function ErrorMessage({ message }) {
  return (
    <div className="error-message glass" role="alert">
      {message}
    </div>
  );
}
