import Header from "../components/Header";
import Banner from '../components/Banner';
import LinkList from "../components/LinkList";
import About from '../components/About';

const Home = () => {

  return (
    <>
      <Header />
      <main>
        <Banner />
        <LinkList />
        <About />
      </main>
    </>
  );
};

export default Home;
