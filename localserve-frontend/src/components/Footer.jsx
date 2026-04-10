const Footer = () => {
  return (
    <footer className="bg-dark text-white-50 py-3 mt-auto">
      <div className="container d-flex justify-content-between align-items-center">
        <span className="small">© {new Date().getFullYear()} LocalServe</span>
        <span className="small">Find local services near you</span>
      </div>
    </footer>
  );
};

export default Footer;