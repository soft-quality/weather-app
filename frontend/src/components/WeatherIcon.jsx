const ICONS = {
  sun: (
    <>
      <circle cx="12" cy="12" r="4.5" />
      <path d="M12 2.5v2M12 19.5v2M4.2 4.2l1.4 1.4M18.4 18.4l1.4 1.4M2.5 12h2M19.5 12h2M4.2 19.8l1.4-1.4M18.4 5.6l1.4-1.4" />
    </>
  ),
  moon: <path d="M20 14.5A8.5 8.5 0 1 1 9.5 4a7 7 0 0 0 10.5 10.5Z" />,
  'cloud-sun': (
    <>
      <circle cx="8" cy="8" r="3" />
      <path d="M8 2.5v1.5M13.5 8H15M3 8h1.5M4.6 4.6l1 1M11.4 4.6l-1 1" />
      <path d="M8.5 20h8a3.5 3.5 0 0 0 .5-6.96A5 5 0 0 0 7.6 13.2 3.5 3.5 0 0 0 8.5 20Z" />
    </>
  ),
  cloud: <path d="M7 19h10a4 4 0 0 0 .5-7.97A6 6 0 0 0 6 12.2 4 4 0 0 0 7 19Z" />,
  fog: (
    <>
      <path d="M7 15h10a4 4 0 0 0 .3-7.98A6 6 0 0 0 6.1 8.4 4 4 0 0 0 7 15Z" />
      <path d="M4 19h16M6 22h12" />
    </>
  ),
  drizzle: (
    <>
      <path d="M7 13h10a4 4 0 0 0 .3-7.98A6 6 0 0 0 6.1 6.4 4 4 0 0 0 7 13Z" />
      <path d="M8 17.5 7 20M12 17.5l-1 2.5M16 17.5l-1 2.5" />
    </>
  ),
  rain: (
    <>
      <path d="M7 13h10a4 4 0 0 0 .3-7.98A6 6 0 0 0 6.1 6.4 4 4 0 0 0 7 13Z" />
      <path d="M8 17l-1.5 3.5M12.5 17 11 20.5M17 17l-1.5 3.5" />
    </>
  ),
  snow: (
    <>
      <path d="M7 13h10a4 4 0 0 0 .3-7.98A6 6 0 0 0 6.1 6.4 4 4 0 0 0 7 13Z" />
      <path d="M8 17v4M12 17v4M16 17v4M6.5 19h3M10.5 19h3M14.5 19h3" />
    </>
  ),
  storm: (
    <>
      <path d="M7 12h10a4 4 0 0 0 .3-7.98A6 6 0 0 0 6.1 5.4 4 4 0 0 0 7 12Z" />
      <path d="M13 13.5 10 18h3l-2 4.5" />
    </>
  ),
};

export default function WeatherIcon({ icon, className = '', size = 48 }) {
  const paths = ICONS[icon] ?? ICONS.cloud;

  return (
    <svg
      className={className}
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      {paths}
    </svg>
  );
}
